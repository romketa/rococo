package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import java.util.UUID;

public record PaintingJson(

    @JsonProperty("id")
    UUID id,

    @JsonProperty("title")
    String title,

    @JsonProperty("description")
    String description,

    @JsonProperty("content")
    byte[] content,

    @JsonProperty("museum")
    MuseumJson museumJson,

    @JsonProperty("artist")
    ArtistJson artistJson
) {

  public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity entity) {
    MuseumJson museum = MuseumJson.fromEntity(entity.getMuseum());
    ArtistJson artist = ArtistJson.fromEntity(entity.getArtist());
    return new PaintingJson(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getContent(),
        museum,
        artist
    );
  }
}
