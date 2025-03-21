package guru.qa.rococo.model;


import static com.google.protobuf.ByteString.copyFromUtf8;
import static java.util.UUID.fromString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AddMuseumRequest;
import guru.qa.grpc.rococo.CountryId;
import guru.qa.grpc.rococo.Geo;
import guru.qa.grpc.rococo.MuseumResponse;
import guru.qa.rococo.data.entity.ArtistEntity;
import guru.qa.rococo.data.entity.MuseumEntity;
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
        GeoJson geo
) {

    public static MuseumJson fromGrpcMessage(MuseumResponse response) {
        CountryJson country = new CountryJson(fromString(response.getGeo().getCountry().getId().toStringUtf8()), null);
        return new MuseumJson(
                fromString(response.getId().toStringUtf8()),
                response.getTitle(),
                response.getDescription(),
                response.getPhoto().toStringUtf8(),
                new GeoJson(response.getGeo().getCity(), country)
        );
    }

  public static MuseumJson fromEntity(MuseumEntity museumEntity) {
    CountryJson country = new CountryJson(museumEntity.getCountryId(), null);
    return new MuseumJson(
        museumEntity.getId(),
        museumEntity.getTitle(),
        museumEntity.getDescription(),
        ByteString.copyFrom(museumEntity.getPhoto()).toStringUtf8(),
        new GeoJson(museumEntity.getCity(), country)
    );
  }
}
