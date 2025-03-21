package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.CountryResponse;
import guru.qa.rococo.data.entity.ArtistEntity;
import guru.qa.rococo.data.entity.CountryEntity;
import java.util.UUID;

public record CountryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name
) {

    public static CountryJson fromGrpcMessage(CountryResponse response) {
        return new CountryJson(
                UUID.fromString(response.getId().toStringUtf8()),
                response.getName()
        );
    }

    public static CountryJson fromEntity(CountryEntity countryEntity) {
        return new CountryJson(
            countryEntity.getId(),
            countryEntity.getName()
        );
    }
}
