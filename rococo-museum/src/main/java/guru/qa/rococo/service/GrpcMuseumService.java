package guru.qa.rococo.service;

import static io.grpc.Status.NOT_FOUND;
import static java.util.UUID.fromString;

import guru.qa.grpc.rococo.AddMuseumRequest;
import guru.qa.grpc.rococo.AllMuseumByIdsResponse;
import guru.qa.grpc.rococo.AllMuseumsRequest;
import guru.qa.grpc.rococo.AllMuseumsResponse;
import guru.qa.grpc.rococo.EditMuseumRequest;
import guru.qa.grpc.rococo.MuseumIdsRequest;
import guru.qa.grpc.rococo.MuseumRequest;
import guru.qa.grpc.rococo.MuseumResponse;
import guru.qa.grpc.rococo.RococoMuseumServiceGrpc;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.model.LogJson;
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
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcMuseumService.class);
  private final MuseumRepository museumRepository;
  private final KafkaTemplate<String, LogJson> kafkaTemplate;

  public GrpcMuseumService(MuseumRepository museumRepository, KafkaTemplate<String, LogJson> kafkaTemplate) {
    this.museumRepository = museumRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void getMuseum(MuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
    UUID museumId = fromString(request.getId().toStringUtf8());
    museumRepository.findById(museumId)
        .ifPresentOrElse(
            ae -> {
              MuseumResponse response = MuseumEntity.toGrpcMessage(ae);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            },
            () -> responseObserver.onError(
                NOT_FOUND.withDescription("Museum not found by id: " + museumId)
                    .asRuntimeException()
            )
        );
  }

  @Override
  public void getAllMuseums(AllMuseumsRequest request,
      StreamObserver<AllMuseumsResponse> responseObserver) {
    int page = request.getPage();
    int size = request.getSize();
    PageRequest pageable = PageRequest.of(page, size);
    String title = request.getTitle();
    Page<MuseumEntity> museumPage = museumRepository.findByTitleContainingIgnoreCase(title, pageable);

    AllMuseumsResponse.Builder responseBuilder = AllMuseumsResponse.newBuilder();
    museumPage.forEach(museumEntity -> {
      MuseumResponse museumResponse = MuseumEntity.toGrpcMessage(museumEntity);
      responseBuilder.addMuseum(museumResponse);
    });
    responseBuilder.setMuseumsCount((int) museumPage.getTotalElements());

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void addMuseum(AddMuseumRequest addMuseumRequest,
      StreamObserver<MuseumResponse> responseObserver) {
    MuseumEntity entity = museumRepository.save(
        MuseumEntity.fromAddMuseumGrpcMessage(addMuseumRequest));
    responseObserver.onNext(MuseumEntity.toGrpcMessage(entity));
    responseObserver.onCompleted();
    LogJson log = new LogJson(
        "Museum",
        entity.getId(),
        "Museum " + entity.getTitle() + " was successfully added",
        EventType.NEW_ENTITY,
        LocalDateTime.now()
    );
    kafkaTemplate.send("museums", log);
    LOGGER.info("### Kafka topic [museums] sent message: {} {}", entity.getTitle(),
        EventType.NEW_ENTITY);
  }

  @Override
  public void editMuseum(EditMuseumRequest request,
      StreamObserver<MuseumResponse> responseObserver) {
    UUID museumId = fromString(request.getId().toStringUtf8());
    museumRepository.findById(museumId)
        .ifPresentOrElse(
            museumEntity -> {
              museumEntity = MuseumEntity.fromEditMuseumGrpcMessage(request);
              museumRepository.save(museumEntity);
              responseObserver.onNext(MuseumEntity.toGrpcMessage(museumEntity));
              responseObserver.onCompleted();

              LogJson log = new LogJson(
                  "Museum",
                  museumEntity.getId(),
                  "Museum " + museumEntity.getTitle() + " was successfully updated",
                  EventType.EDIT_ENTITY,
                  LocalDateTime.now()
              );
              kafkaTemplate.send("museums", log);
              LOGGER.info("### Kafka topic [museums] sent message: {} {}", museumEntity.getTitle(),
                  EventType.EDIT_ENTITY);
            }, () -> responseObserver.onError(
                NOT_FOUND.withDescription("Museum not found by id: " + museumId)
                    .asRuntimeException()
            )
        );
  }

  @Override
  public void getMuseumByIds(MuseumIdsRequest request,
      StreamObserver<AllMuseumByIdsResponse> responseObserver) {
    Set<UUID> museumIds = request.getIdList().stream()
        .map(byteString -> fromString(byteString.toStringUtf8()))
        .collect(Collectors.toSet());

    List<MuseumEntity> museums = museumRepository.findAllById(museumIds);

    AllMuseumByIdsResponse.Builder responseBuilder = AllMuseumByIdsResponse.newBuilder();
    museums.forEach(museumEntity -> {
      MuseumResponse response = MuseumEntity.toGrpcMessage(museumEntity);
      responseBuilder.addMuseum(response);
    });

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }
}