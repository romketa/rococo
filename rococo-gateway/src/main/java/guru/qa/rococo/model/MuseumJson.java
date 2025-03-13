package guru.qa.rococo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static java.util.UUID.fromString;

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

    public static AddMuseumRequest toGrpcMessage(MuseumJson museumJson) {
        CountryId countryId = CountryId.newBuilder()
                .setId(copyFromUtf8(museumJson.geo().getCountry().id().toString()))
                .build();
        Geo geo = Geo.newBuilder()
                .setCity(museumJson.geo().getCity())
                .setCountry(countryId)
                .build();
        return AddMuseumRequest.newBuilder()
                .setTitle(museumJson.title())
                .setDescription(museumJson.description())
                .setPhoto(ByteString.copyFrom(museumJson.photo(), StandardCharsets.UTF_8))
                .setGeo(geo)
                .build();
    }
}
