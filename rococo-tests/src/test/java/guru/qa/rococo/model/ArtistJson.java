package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.ArtistResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import guru.qa.rococo.data.entity.ArtistEntity;

public record ArtistJson
    (
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo
    ) {

  public static ArtistJson fromGrpcMessage(ArtistResponse response) {
    return new ArtistJson(
        UUID.fromString(response.getId().toStringUtf8()),
        response.getName(),
        response.getBiography(),
        response.getPhoto().toStringUtf8()
    );
  }

  public static ArtistJson fromEntity(ArtistEntity artistEntity) {
    return new ArtistJson(
        artistEntity.getId(),
        artistEntity.getName(),
        artistEntity.getBiography(),
        ByteString.copyFrom(artistEntity.getPhoto()).toStringUtf8()
    );
  }
}
