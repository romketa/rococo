package guru.qa.rococo.test.grpc;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.RandomDataUtils.getRandomCountry;
import static utils.RandomDataUtils.randomCity;
import static utils.RandomDataUtils.randomDescription;
import static utils.RandomDataUtils.randomMuseumTitle;
import static utils.Utils.toByteStringFromUuid;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AddMuseumRequest;
import guru.qa.grpc.rococo.AllMuseumByIdsResponse;
import guru.qa.grpc.rococo.CountryId;
import guru.qa.grpc.rococo.EditMuseumRequest;
import guru.qa.grpc.rococo.Geo;
import guru.qa.grpc.rococo.MuseumIdsRequest;
import guru.qa.grpc.rococo.MuseumRequest;
import guru.qa.grpc.rococo.MuseumResponse;
import guru.qa.grpc.rococo.RococoMuseumServiceGrpc;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.MuseumJson;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.GrpcConsoleInterceptor;
import utils.ImageUtils;

@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.GRPC)
@Feature(AllureFeature.MUSEUM)
@Tag(JTag.GRPC)
@DisplayName("GRPC: Rococo-museum service tests")
public class MuseumTest extends BaseTest {

  private static final Channel museumChannel = ManagedChannelBuilder
      .forAddress(CFG.museumGrpcAddress(), CFG.museumGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  private static final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub
      = RococoMuseumServiceGrpc.newBlockingStub(museumChannel);

  private static final String MUSEUM_PHOTO_PATH = "img/museums/luvr.jpg";

  @Test
  @DisplayName("Verify adding a new museum")
  void verifyAddingMuseum() {
    String title = randomMuseumTitle();
    String description = randomDescription();

    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(title)
        .setDescription(description)
        .setPhoto(copyFromUtf8(ImageUtils.convertImgToBase64(MUSEUM_PHOTO_PATH)))
        .setGeo(getRandomGeo())
        .build();

    MuseumResponse response = museumStub.addMuseum(request);

    step("Check that museum is added correctly", () -> {
      assertEquals(title, response.getTitle());
      assertEquals(description, response.getDescription());
    });
  }

  @Test
  @DisplayName("Verify error in case museum not found by ID")
  void verifyErrorIfMuseumNotFoundById() {
    String randomId = UUID.randomUUID().toString();
    MuseumRequest request = MuseumRequest.newBuilder()
        .setId(copyFromUtf8(randomId))
        .build();

    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> museumStub.getMuseum(request));

    step("Check that museum was not found",
        () -> assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode()));
    step("Check message for not found artist",
        () -> assertEquals("Museum not found by id: " + randomId,
            exception.getStatus().getDescription()));
  }

  @Test
  @Museum
  @DisplayName("Verify editing an existing museum")
  void verifyEditingMuseum(MuseumJson museum) {
    String title = randomMuseumTitle();
    String description = randomDescription();
    String city = randomCity();
    UUID countryId = getRandomCountry().id();
    ByteString content = copyFromUtf8(MUSEUM_PHOTO_PATH);
    Geo geo = Geo.newBuilder()
        .setCity(city)
        .setCountry(CountryId.newBuilder()
            .setId(toByteStringFromUuid(countryId)))
        .build();
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(title)
        .setDescription(description)
        .setPhoto(content)
        .setGeo(geo)
        .build();

    EditMuseumRequest editRequest = EditMuseumRequest.newBuilder()
        .setId(toByteStringFromUuid(museum.id()))
        .setMuseumData(request)
        .build();

    MuseumResponse updatedMuseum = museumStub.editMuseum(editRequest);

    step("Check that museum is updated correctly", () -> {
      assertEquals(title, updatedMuseum.getTitle());
      assertEquals(description, updatedMuseum.getDescription());
      assertEquals(content, updatedMuseum.getPhoto());
      assertEquals(city, updatedMuseum.getGeo().getCity());
      assertEquals(toByteStringFromUuid(countryId), updatedMuseum.getGeo().getCountry().getId());
    });
  }

  @Test
  @Museum
  @DisplayName("Verify retrieving museum by ID")
  void verifyGettingMuseumById(MuseumJson museum) {
    MuseumRequest getRequest = MuseumRequest.newBuilder()
        .setId(toByteStringFromUuid(museum.id()))
        .build();

    MuseumResponse fetchedMuseum = museumStub.getMuseum(getRequest);

    step("Check that fetched museum matches created museum", () -> {
      assertEquals(toByteStringFromUuid(museum.id()), fetchedMuseum.getId());
      assertEquals(museum.title(), fetchedMuseum.getTitle());
      assertEquals(museum.description(), fetchedMuseum.getDescription());
      assertEquals(museum.geo().city(), fetchedMuseum.getGeo().getCity());
      assertEquals(toByteStringFromUuid(museum.geo().country().id()),
          fetchedMuseum.getGeo().getCountry().getId());
    });
  }

  @Test
  @DisplayName("Verify retrieving multiple museums by IDs")
  void verifyGettingMuseumsByIds() {
    ByteString content = copyFromUtf8(ImageUtils.convertImgToBase64(MUSEUM_PHOTO_PATH));

    AddMuseumRequest request1 = AddMuseumRequest.newBuilder()
        .setTitle(randomMuseumTitle())
        .setDescription(randomDescription())
        .setPhoto(content)
        .setGeo(getRandomGeo())
        .build();

    AddMuseumRequest request2 = AddMuseumRequest.newBuilder()
        .setTitle(randomMuseumTitle())
        .setDescription(randomDescription())
        .setPhoto(content)
        .setGeo(getRandomGeo())
        .build();

    MuseumResponse museum1 = museumStub.addMuseum(request1);
    MuseumResponse museum2 = museumStub.addMuseum(request2);

    MuseumIdsRequest idsRequest = MuseumIdsRequest.newBuilder()
        .addId(museum1.getId())
        .addId(museum2.getId())
        .build();

    AllMuseumByIdsResponse response = museumStub.getMuseumByIds(idsRequest);

    step("Check that response contains both museums", () -> {
      assertEquals(2, response.getMuseumList().size());
    });
  }

  @Nonnull
  private Geo getRandomGeo() {
    String city = randomCity();
    UUID countryId = getRandomCountry().id();
    return Geo.newBuilder()
        .setCity(city)
        .setCountry(CountryId.newBuilder()
            .setId(toByteStringFromUuid(countryId)))
        .build();
  }
}
