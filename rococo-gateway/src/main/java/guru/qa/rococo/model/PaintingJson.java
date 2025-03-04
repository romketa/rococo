package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PaintingJson(

    @JsonProperty("id")
    UUID id,

    @JsonProperty("title")
    String title,

    @JsonProperty("description")
    String description,

    @JsonProperty("content")
    String content,

    @JsonProperty("museum")
    MuseumJson museumJson,

    @JsonProperty("artist")
    ArtistJson artistJson
) {

  public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity entity) {
    MuseumJson museum = new MuseumJson(entity.getMuseumId(), null, null, null, null);
    ArtistJson artist = new ArtistJson(entity.getArtistId(), null, null, null);
    return new PaintingJson(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getContent() != null && entity.getContent().length > 0 ? new String(
            entity.getContent(),
            StandardCharsets.UTF_8) : null,
        museum,
        artist
    );
  }
}
