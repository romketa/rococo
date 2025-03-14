package guru.qa.rococo.service;

import static com.google.protobuf.ByteString.copyFromUtf8;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AddArtistRequest;
import guru.qa.grpc.rococo.AllArtistByIdsResponse;
import guru.qa.grpc.rococo.AllArtistRequest;
import guru.qa.grpc.rococo.AllArtistsResponse;
import guru.qa.grpc.rococo.ArtistIdsRequest;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.grpc.rococo.ArtistResponse;
import guru.qa.grpc.rococo.EditArtistRequest;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.rococo.model.ArtistJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GrpcArtistClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistClient.class);

  private final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub;

  @Autowired
  public GrpcArtistClient(RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub) {
    this.artistStub = artistStub;
  }

  public @Nonnull ArtistJson getArtist(UUID id) {
    ArtistRequest request = ArtistRequest.newBuilder()
        .setId(copyFromUtf8(id.toString()))
        .build();
    try {
      ArtistResponse response = artistStub.getArtist(request);
      return ArtistJson.fromGrpcMessage(response);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Can`t find artist by id " + id, e);
      } else {
        LOG.error("### Error while calling gRPC server ", e);
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
            "The gRPC operation was cancelled", e);
      }
    }
  }

  public @Nonnull Page<ArtistJson> getAllArtist(@Nullable String name, Pageable pageable) {
    AllArtistRequest.Builder builder = AllArtistRequest.newBuilder()
        .setPage(pageable.getPageNumber())
        .setSize(pageable.getPageSize());
    if (name != null) {
      builder.setName(name);
    }
    AllArtistRequest request = builder.build();

    try {
      AllArtistsResponse response = artistStub.getAllArtists(request);
      List<ArtistJson> artistJsonList = response.getArtists1List()
          .stream()
          .map(ArtistJson::fromGrpcMessage)
          .toList();
      return new PageImpl<>(artistJsonList, pageable, response.getArtistsCount2());
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "The gRPC operation was cancelled", e);
    }
  }

  public @Nonnull ArtistJson addArtist(ArtistJson artist) {
    AddArtistRequest request = ArtistJson.toGrpcMessage(artist);
    ArtistResponse response = artistStub.addArtist(request);
    return ArtistJson.fromGrpcMessage(response);
  }

  public @Nonnull ArtistJson editArtist(ArtistJson artist) {
    AddArtistRequest artistData = ArtistJson.toGrpcMessage(artist);
    EditArtistRequest request = EditArtistRequest.newBuilder()
        .setId(ByteString.copyFromUtf8(artist.id().toString()))
        .setArtistData(artistData)
        .build();
    ArtistResponse response = artistStub.editArtist(request);
    return ArtistJson.fromGrpcMessage(response);
  }

  @Nonnull
  List<ArtistJson> getArtistByIds(Set<UUID> museumIds) {
    ArtistIdsRequest.Builder requestBuilder = ArtistIdsRequest.newBuilder();
    museumIds.forEach(
        museumId -> requestBuilder.addId(ByteString.copyFromUtf8(museumId.toString())));
    ArtistIdsRequest request = requestBuilder.build();
    try {
      AllArtistByIdsResponse response = artistStub.getArtistByIds(request);
      return response.getArtistList()
          .stream()
          .map(ArtistJson::fromGrpcMessage)
          .toList();
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "The gRPC operation was cancelled", e);
    }
  }

}