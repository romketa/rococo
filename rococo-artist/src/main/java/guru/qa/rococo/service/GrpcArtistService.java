package guru.qa.rococo.service;

import static java.util.UUID.fromString;

import guru.qa.grpc.rococo.AddArtistRequest;
import guru.qa.grpc.rococo.AllArtistByIdsResponse;
import guru.qa.grpc.rococo.AllArtistRequest;
import guru.qa.grpc.rococo.AllArtistsResponse;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.grpc.rococo.ArtistResponse;
import guru.qa.grpc.rococo.ArtistsIdRequest;
import guru.qa.grpc.rococo.EditArtistRequest;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

  private final ArtistRepository artistRepository;

  public GrpcArtistService(ArtistRepository artistRepository) {
    this.artistRepository = artistRepository;
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
                Status.NOT_FOUND.withDescription("Artist not found by id " + artistId)
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

    Page<ArtistEntity> artistPage = artistRepository.findAll(pageable);

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
    ArtistEntity newArtist = new ArtistEntity();
    setArtistData(newArtist, addArtistRequest);
    ArtistEntity ae = artistRepository.save(newArtist);
    responseObserver.onNext(ArtistEntity.toGrpcMessage(ae));
    responseObserver.onCompleted();
  }

  @Override
  public void editArtist(EditArtistRequest request,
      StreamObserver<ArtistResponse> responseObserver) {
    UUID artistId = fromString(request.getId().toStringUtf8());
    artistRepository.findById(artistId)
        .ifPresentOrElse(
            ae -> {
              setArtistData(ae, request.getArtistData());
              artistRepository.save(ae);
              responseObserver.onNext(ArtistEntity.toGrpcMessage(ae));
              responseObserver.onCompleted();
            },
            () -> responseObserver.onError(
                Status.NOT_FOUND.withDescription("Artist not found by id " + artistId)
                    .asRuntimeException()
            )
        );
  }

  @Override
  public void getArtistById(ArtistsIdRequest request,
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

  private static void setArtistData(ArtistEntity artist, AddArtistRequest addArtistRequest) {
    artist.setName(addArtistRequest.getName());
    artist.setBiography(addArtistRequest.getBiography());
    artist.setPhoto(addArtistRequest.toByteArray());
  }
}
