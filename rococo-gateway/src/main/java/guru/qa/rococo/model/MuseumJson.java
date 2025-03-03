package guru.qa.rococo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("title")
    String title,

    @JsonProperty("description")
    String description,

    @JsonProperty("photo")
    String photo,

    @JsonProperty("geo")
    Geo geo
) {

  public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity entity) {
    final CountryJson country = CountryJson.fromEntity(entity.getCountryEntity());
    return new MuseumJson(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(),
            StandardCharsets.UTF_8) : null,
        new Geo("Москва", country)
    );
  }
}
