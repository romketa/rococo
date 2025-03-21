package guru.qa.rococo.model;

import static java.util.UUID.fromString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.PaintingResponse;
import guru.qa.rococo.data.entity.PaintingEntity;
import java.util.UUID;
import javax.annotation.Nonnull;


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
    MuseumJson museum,

    @JsonProperty("artist")
    ArtistJson artist) {

  public static @Nonnull PaintingJson fromGrpcMessage(@Nonnull PaintingResponse response) {
    MuseumJson museum = new MuseumJson(fromString(response.getMuseumId().getId().toStringUtf8()),
        null, null, null, null);
    ArtistJson artist = new ArtistJson(fromString(response.getArtistId().getId().toStringUtf8()),
        null, null, null);
    return new PaintingJson(
        fromString(response.getId().toStringUtf8()),
        response.getTitle(),
        response.getDescription(),
        response.getContent().toStringUtf8(),
        museum,
        artist
    );
  }

  public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity paintingEntity) {
    MuseumJson museum = new MuseumJson(paintingEntity.getMuseumId(), null, null, null, null);
    ArtistJson artist = new ArtistJson(paintingEntity.getArtistId(), null, null, null);
    return new PaintingJson(
        paintingEntity.getId(),
        paintingEntity.getTitle(),
        paintingEntity.getDescription(),
        ByteString.copyFrom(paintingEntity.getContent()).toStringUtf8(),
        museum,
        artist
    );
  }
}
