package guru.qa.rococo.service;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.protobuf.ByteString.copyFromUtf8;

@Component
public class GrpcMuseumClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumClient.class);

    private final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub;
    private final GrpcCountryClient countryClient;

    @Autowired
    public GrpcMuseumClient(RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub,
                            GrpcCountryClient countryClient) {
        this.museumStub = museumStub;
        this.countryClient = countryClient;
    }

    public @Nonnull MuseumJson getMuseum(@Nonnull UUID id) {
        MuseumRequest request = MuseumRequest.newBuilder()
                .setId(copyFromUtf8(id.toString()))
                .build();
        try {
            MuseumResponse response = museumStub.getMuseum(request);
            MuseumJson museumJson = MuseumJson.fromGrpcMessage(response);
            completeMuseumData(museumJson);
            return museumJson;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Can`t find museum by id " + id, e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "The gRPC operation was cancelled", e);
            }
        }
    }

    public @Nonnull Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
        AllMuseumsRequest.Builder builder = AllMuseumsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize());
        AllMuseumsRequest request = builder.build();

        try {
            AllMuseumsResponse response = museumStub.getAllMuseums(request);
            List<MuseumJson> museumJsons = response.getMuseumList()
                    .stream()
                    .map(MuseumJson::fromGrpcMessage)
                    .toList();
            completeMuseumData(museumJsons);
            return new PageImpl<>(museumJsons, pageable, response.getMuseumsCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
        AddMuseumRequest request = MuseumJson.toGrpcMessage(museum);
        MuseumResponse response = museumStub.addMuseum(request);
        MuseumJson museumJson = MuseumJson.fromGrpcMessage(response);
        completeMuseumData(museumJson);
        return museumJson;
    }

    public @Nonnull MuseumJson editMuseum(@Nonnull MuseumJson museum) {
        AddMuseumRequest museumData = MuseumJson.toGrpcMessage(museum);
        EditMuseumRequest request = EditMuseumRequest.newBuilder()
                .setId(ByteString.copyFromUtf8(museum.id().toString()))
                .setMuseumData(museumData)
                .build();
        MuseumResponse response = museumStub.editMuseum(request);
        MuseumJson museumJson = MuseumJson.fromGrpcMessage(response);
        completeMuseumData(museumJson);
        return museumJson;
    }

    @Nonnull
    public List<MuseumJson> getMuseumByIds(Set<UUID> museumIds) {
        MuseumIdsRequest.Builder requestBuilder = MuseumIdsRequest.newBuilder();
        museumIds.forEach(museumId -> requestBuilder.addId(ByteString.copyFromUtf8(museumId.toString())));
        MuseumIdsRequest request = requestBuilder.build();
        try {
            AllMuseumByIdsResponse response = museumStub.getMuseumByIds(request);
            List<MuseumJson> museumJsonList = response.getMuseumList()
                    .stream()
                    .map(MuseumJson::fromGrpcMessage)
                    .toList();
            completeMuseumData(museumJsonList);
            return museumJsonList;
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    private void completeMuseumData(MuseumJson museum) {
        CountryJson country = countryClient.getCountry(museum.geo().getCountry().id());
        museum.geo().setCountry(country);
    }

    private void completeMuseumData(List<MuseumJson> museumJsonList) {
        Set<UUID> countryIds = museumJsonList.stream()
                .map(museum -> museum.geo().getCountry().id())
                .collect(Collectors.toSet());

        List<CountryJson> countries = countryClient.getCountryByIds(countryIds);

        museumJsonList.forEach(museum -> {
            UUID countryId = museum.geo().getCountry().id();
            Optional<CountryJson> matchingCountry = countries.stream()
                    .filter(country -> country.id().equals(countryId))
                    .findFirst();
            matchingCountry.ifPresent(country -> museum.geo().setCountry(country));
        });
    }

}
