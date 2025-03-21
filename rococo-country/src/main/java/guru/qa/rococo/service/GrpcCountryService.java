package guru.qa.rococo.service;

import static io.grpc.Status.NOT_FOUND;
import static java.util.UUID.fromString;

import guru.qa.grpc.rococo.AllCountriesRequest;
import guru.qa.grpc.rococo.AllCountriesResponse;
import guru.qa.grpc.rococo.AllCountryByIdsResponse;
import guru.qa.grpc.rococo.CountryId;
import guru.qa.grpc.rococo.CountryIdsRequest;
import guru.qa.grpc.rococo.CountryName;
import guru.qa.grpc.rococo.CountryResponse;
import guru.qa.grpc.rococo.RococoCountryServiceGrpc;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GrpcCountryService extends RococoCountryServiceGrpc.RococoCountryServiceImplBase {

  private final CountryRepository countryRepository;

  public GrpcCountryService(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @Override
  public void getCountry(CountryId request, StreamObserver<CountryResponse> responseObserver) {
    UUID countryId = fromString(request.getId().toStringUtf8());
    countryRepository.findById(countryId)
        .ifPresentOrElse(
            ae -> {
              CountryResponse response = CountryEntity.toGrpcMessage(ae);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            },
            () -> responseObserver.onError(
                NOT_FOUND.withDescription("Country not found by id: " + countryId)
                    .asRuntimeException()
            )
        );
  }

  @Override
  public void getAllCountries(AllCountriesRequest request,
      StreamObserver<AllCountriesResponse> responseObserver) {
    int page = request.getPage();
    int size = request.getSize();
    PageRequest pageable = PageRequest.of(page, size);

    Page<CountryEntity> countryPage = countryRepository.findAll(pageable);

    AllCountriesResponse.Builder responseBuilder = AllCountriesResponse.newBuilder();
    countryPage.forEach(countryEntity -> {
      CountryResponse countryResponse = CountryEntity.toGrpcMessage(countryEntity);
      responseBuilder.addCountry(countryResponse);
    });
    responseBuilder.setCountriesCount((int) countryPage.getTotalElements());

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void getCountryByName(CountryName request,
      StreamObserver<CountryResponse> responseObserver) {
    String countryName = request.getName();
    countryRepository.getByName(countryName).ifPresentOrElse(
        ae -> {
          CountryResponse response = CountryEntity.toGrpcMessage(ae);
          responseObserver.onNext(response);
          responseObserver.onCompleted();
        },
        () -> responseObserver.onError(
            NOT_FOUND.withDescription("Country not found by name: " + countryName)
                .asRuntimeException()
        )
    );
  }

  @Override
  public void getCountriesByIds(CountryIdsRequest request,
      StreamObserver<AllCountryByIdsResponse> responseObserver) {
    Set<UUID> countryIds = request.getIdList().stream()
        .map(byteString -> fromString(byteString.toStringUtf8()))
        .collect(Collectors.toSet());

    List<CountryEntity> countries = countryRepository.findAllById(countryIds);

    AllCountryByIdsResponse.Builder responseBuilder = AllCountryByIdsResponse.newBuilder();
    countries.forEach(countryEntity -> {
      CountryResponse response = CountryEntity.toGrpcMessage(countryEntity);
      responseBuilder.addCountry(response);
    });

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }
}
