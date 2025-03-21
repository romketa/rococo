package guru.qa.rococo.test.grpc;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.RandomDataUtils.getRandomCountry;
import static utils.RandomDataUtils.randomArtistName;
import static utils.RandomDataUtils.randomDescription;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AddArtistRequest;
import guru.qa.grpc.rococo.AllCountriesRequest;
import guru.qa.grpc.rococo.AllCountriesResponse;
import guru.qa.grpc.rococo.AllCountryByIdsResponse;
import guru.qa.grpc.rococo.ArtistIdsRequest;
import guru.qa.grpc.rococo.ArtistResponse;
import guru.qa.grpc.rococo.CountryId;
import guru.qa.grpc.rococo.CountryIdsRequest;
import guru.qa.grpc.rococo.CountryName;
import guru.qa.grpc.rococo.CountryResponse;
import guru.qa.grpc.rococo.RococoCountryServiceGrpc;
import guru.qa.rococo.jupiter.annotation.Country;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.CountryJson;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.GrpcConsoleInterceptor;
import utils.ImageUtils;

@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.GRPC)
@Feature(AllureFeature.COUNTRY)
@Tag(JTag.GRPC)
@DisplayName("GRPC: Rococo-country service tests")
public class CountryTest extends BaseTest{

  private static final Channel countryChannel = ManagedChannelBuilder
      .forAddress(CFG.grpcAddress(), CFG.countryGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  private static final RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryStub
      = RococoCountryServiceGrpc.newBlockingStub(countryChannel);

  @Test
  @Country
  @DisplayName("Verify getting country by ID")
  void verifyGettingCountryById(CountryJson country) {
    CountryId request = CountryId.newBuilder()
        .setId(copyFromUtf8(country.id().toString()))
        .build();

    CountryResponse response = countryStub.getCountry(request);

    step("Check that country details match", () -> {
      assertEquals(country.id().toString(), response.getId().toStringUtf8());
      assertEquals(country.name(), response.getName());
    });
  }

  @Test
  @DisplayName("Verify error in case country not found by ID")
  void verifyErrorIfCountryNotFoundById() {
    String randomId = UUID.randomUUID().toString();
    CountryId request = CountryId.newBuilder()
        .setId(copyFromUtf8(randomId))
        .build();

    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> countryStub.getCountry(request));

    step("Check that country was not found",
        () -> assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode()));
    step("Check message for not found country",
        () -> assertEquals("Country not found by id: " + randomId,
            exception.getStatus().getDescription()));
  }

  @Test
  @Country
  @DisplayName("Verify getting country by name")
  void verifyGettingCountryByName(CountryJson country) {
    CountryName request = CountryName.newBuilder()
        .setName(country.name())
        .build();

    CountryResponse response = countryStub.getCountryByName(request);

    step("Check that country details match", () -> {
      assertEquals(country.name(), response.getName());
      assertEquals(country.id().toString(), response.getId().toStringUtf8());
    });
  }

  @Test
  @DisplayName("Verify getting all countries with pagination")
  void verifyGettingAllCountries() {
    AllCountriesRequest request = AllCountriesRequest.newBuilder()
        .setPage(0)
        .setSize(3)
        .build();

    AllCountriesResponse response = countryStub.getAllCountries(request);

    step("Check that response contains expected number of countries", () -> {
      assertTrue(response.getCountryList().size() <= 3);
    });
  }

  @Test
  @DisplayName("Verify getting multiple countries by IDs")
  void verifyGettingCountriesByIds() {
    List<CountryJson> countries = List.of(getRandomCountry(), getRandomCountry());
    Set<String> countryIds = countries.stream()
        .map(country -> country.id().toString())
        .collect(Collectors.toSet());

    CountryIdsRequest idsRequest = CountryIdsRequest.newBuilder()
        .addAllId(countryIds.stream().map(ByteString::copyFromUtf8).collect(Collectors.toList()))
        .build();

    AllCountryByIdsResponse response = countryStub.getCountriesByIds(idsRequest);

    step("Check that response contains all requested countries", () -> {
      assertEquals(countries.size(), response.getCountryList().size());
    });
  }
}
