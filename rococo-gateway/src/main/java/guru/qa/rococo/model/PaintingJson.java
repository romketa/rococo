package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static java.util.UUID.fromString;

@Getter
@Setter
@Builder
public class PaintingJson {

    @JsonProperty("id")
    UUID id;

    @JsonProperty("title")
    String title;

    @JsonProperty("description")
    String description;

    @JsonProperty("content")
    String content;

    @NotNull(message = "Museum can not be null")
    @JsonProperty("museum")
    MuseumJson museum;

    @NotNull(message = "Artist can not be null")
    @JsonProperty("artist")
    ArtistJson artist;

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

    public static @Nonnull PaintingJson fromGrpcMessage(@Nonnull PaintingResponse response) {
        MuseumJson museum = new MuseumJson(fromString(response.getMuseumId().getId().toStringUtf8()), null, null, null, null);
        ArtistJson artist = new ArtistJson(fromString(response.getArtistId().getId().toStringUtf8()), null, null, null);
        return new PaintingJson(
                fromString(response.getId().toStringUtf8()),
                response.getTitle(),
                response.getDescription(),
                response.getContent().toStringUtf8(),
                museum,
                artist
        );
    }

    public static AddPaintingRequest toGrpcMessage(PaintingJson paintingJson) {
        return AddPaintingRequest.newBuilder()
                .setTitle(paintingJson.getTitle())
                .setDescription(paintingJson.getDescription())
                .setContent(ByteString.copyFrom(paintingJson.getContent(), StandardCharsets.UTF_8))
                .setMuseumId(MuseumId.newBuilder().setId(copyFromUtf8(paintingJson.getMuseum().id().toString())).build())
                .setArtistId(ArtistId.newBuilder().setId(copyFromUtf8(paintingJson.getArtist().id().toString())).build())
                .build();
    }
}
