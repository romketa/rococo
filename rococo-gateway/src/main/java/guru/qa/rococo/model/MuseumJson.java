package guru.qa.rococo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;
import java.util.UUID;

public record MuseumJson(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("title")
    String title,

    @JsonProperty("description")
    String description,

    @JsonProperty("photo")
    byte[] photo,

    @JsonProperty("geo")
    Geo geo
) {

  public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity entity) {
    final CountryJson country = CountryJson.fromEntity(entity.getCountryEntity());
    return new MuseumJson(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getPhoto(),
        new Geo("Москва", country)
    );
  }
}
