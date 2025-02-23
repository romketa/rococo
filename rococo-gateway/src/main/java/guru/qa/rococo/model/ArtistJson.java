package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.ArtistEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import java.util.UUID;

public record ArtistJson
    (
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        byte[] photo
    ) {

  public static @Nonnull ArtistJson fromEntity(@Nonnull ArtistEntity entity) {
    return new ArtistJson(
        entity.getId(),
        entity.getName(),
        entity.getBiography(),
        entity.getPhoto());
  }
}
