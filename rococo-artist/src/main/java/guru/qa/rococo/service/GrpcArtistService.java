package guru.qa.rococo.service;

import static io.grpc.Status.NOT_FOUND;
import static java.util.UUID.fromString;

import guru.qa.grpc.rococo.AddArtistRequest;
import guru.qa.grpc.rococo.AllArtistByIdsResponse;
import guru.qa.grpc.rococo.AllArtistRequest;
import guru.qa.grpc.rococo.AllArtistsResponse;
import guru.qa.grpc.rococo.ArtistIdsRequest;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.grpc.rococo.ArtistResponse;
import guru.qa.grpc.rococo.EditArtistRequest;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.model.LogJson;
import guru.qa.rococo.model.EventType;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.kafka.core.KafkaTemplate;

@GrpcService
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

  private final ArtistRepository artistRepository;
  private final KafkaTemplate<String, LogJson> kafkaTemplate;
  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcArtistService.class);

  public GrpcArtistService(ArtistRepository artistRepository,
      KafkaTemplate<String, LogJson> kafkaTemplate) {
    this.artistRepository = artistRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void getArtist(ArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
    UUID artistId = fromString(request.getId().toStringUtf8());
    artistRepository.findById(artistId)
        .ifPresentOrElse(
            ae -> {
              ArtistResponse response = ArtistEntity.toGrpcMessage(ae);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            },
            () -> responseObserver.onError(
                NOT_FOUND.withDescription("Artist not found by id: " + artistId)
                    .asRuntimeException()
            )
        );
  }

  @Override
  public void getAllArtists(AllArtistRequest request,
      StreamObserver<AllArtistsResponse> responseObserver) {
    int page = request.getPage();
    int size = request.getSize();
    PageRequest pageable = PageRequest.of(page, size);
    String name = request.getName();

    Page<ArtistEntity> artistPage = artistRepository.findByNameContainingIgnoreCase(name, pageable);

    AllArtistsResponse.Builder responseBuilder = AllArtistsResponse.newBuilder();
    artistPage.forEach(artistEntity -> {
      ArtistResponse artistResponse = ArtistEntity.toGrpcMessage(artistEntity);
      responseBuilder.addArtists1(artistResponse);
    });
    responseBuilder.setArtistsCount2((int) artistPage.getTotalElements());

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void addArtist(AddArtistRequest addArtistRequest,
      StreamObserver<ArtistResponse> responseObserver) {
    ArtistEntity entity = artistRepository.save(
        ArtistEntity.fromAddArtistGrpcMessage(addArtistRequest));
    responseObserver.onNext(ArtistEntity.toGrpcMessage(entity));
    responseObserver.onCompleted();
    LogJson log = new LogJson(
        "Artist",
        entity.getId(),
        "Artist" + entity.getName() + " was successfull added",
        EventType.NEW_ARTIST,
        LocalDateTime.now()
    );
    kafkaTemplate.send("artists", log);
    LOGGER.info("### Kafka topic [artists] sent message: {} {}", entity.getName(),
        EventType.NEW_ARTIST);
  }

  @Override
  public void editArtist(EditArtistRequest request,
      StreamObserver<ArtistResponse> responseObserver) {
    UUID artistId = fromString(request.getId().toStringUtf8());
    artistRepository.findById(artistId)
        .ifPresentOrElse(
            artistEntity -> {
              artistEntity = ArtistEntity.fromEditArtistGrpcMessage(request);
              artistRepository.save(artistEntity);
              responseObserver.onNext(ArtistEntity.toGrpcMessage(artistEntity));
              responseObserver.onCompleted();
            }, () -> responseObserver.onError(
                NOT_FOUND.withDescription("Artist not found by id: " + artistId)
                    .asRuntimeException()
            )
        );
  }

  @Override
  public void getArtistByIds(ArtistIdsRequest request,
      StreamObserver<AllArtistByIdsResponse> responseObserver) {
    Set<UUID> artistIds = request.getIdList().stream()
        .map(bytes -> fromString(bytes.toStringUtf8()))
        .collect(Collectors.toSet());

    List<ArtistEntity> artistEntityList = artistRepository.findAllById(artistIds);

    AllArtistByIdsResponse.Builder builder = AllArtistByIdsResponse.newBuilder();

    artistEntityList.forEach(artistEntity -> {
      ArtistResponse response = ArtistEntity.toGrpcMessage(artistEntity);
      builder.addArtist(response);
    });

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}