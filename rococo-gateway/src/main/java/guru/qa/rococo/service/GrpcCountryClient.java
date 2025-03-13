package guru.qa.rococo.service;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.CountryJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.google.protobuf.ByteString.copyFromUtf8;

@Component
public class GrpcCountryClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcCountryClient.class);

    private final RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryStub;

    @Autowired
    public GrpcCountryClient(RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryStub) {
        this.countryStub = countryStub;
    }

    public @Nonnull CountryJson getCountry(UUID id) {
        CountryId request = CountryId.newBuilder()
                .setId(copyFromUtf8(id.toString()))
                .build();
        try {
            CountryResponse response = countryStub.getCountry(request);
            return CountryJson.fromGrpcMessage(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Can`t find country by id " + id, e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "The gRPC operation was cancelled", e);
            }
        }
    }

    public @Nonnull Page<CountryJson> getAllCountries(Pageable pageable) {
        AllCountriesRequest.Builder builder = AllCountriesRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize());
        AllCountriesRequest request = builder.build();

        try {
            AllCountriesResponse response = countryStub.getAllCountries(request);
            List<CountryJson> countryJsonList = response.getCountryList()
                    .stream()
                    .map(CountryJson::fromGrpcMessage)
                    .toList();
            return new PageImpl<>(countryJsonList, pageable, response.getCountriesCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull CountryJson getCountryByName(String name) {
        CountryName request = CountryName.newBuilder()
                .setName(name)
                .build();
        try {
            CountryResponse response = countryStub.getCountryByName(request);
            return CountryJson.fromGrpcMessage(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Can`t find country by name " + name, e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "The gRPC operation was cancelled", e);
            }
        }
    }

    public @Nonnull List<CountryJson> getCountryByIds(Set<UUID> ids) {
        CountryIdsRequest.Builder requestBuilder = CountryIdsRequest.newBuilder();
        ids.forEach(countryId -> requestBuilder.addId(ByteString.copyFromUtf8(countryId.toString())));
        CountryIdsRequest request = requestBuilder.build();
        try {
            AllCountryByIdsResponse response = countryStub.getCountriesByIds(request);
            return response.getCountryList()
                    .stream()
                    .map(CountryJson::fromGrpcMessage)
                    .toList();
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
