package guru.qa.rococo.test.grpc;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static guru.qa.rococo.model.ArtistJson.fromGrpcMessage;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.RandomDataUtils.randomArtistName;
import static utils.RandomDataUtils.randomDescription;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AddArtistRequest;
import guru.qa.grpc.rococo.AllArtistByIdsResponse;
import guru.qa.grpc.rococo.AllArtistRequest;
import guru.qa.grpc.rococo.AllArtistsResponse;
import guru.qa.grpc.rococo.ArtistIdsRequest;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.grpc.rococo.ArtistResponse;
import guru.qa.grpc.rococo.EditArtistRequest;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.ArtistJson;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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
@Feature(AllureFeature.ARTIST)
@Tag(JTag.GRPC)
@DisplayName("GRPC: Rococo-artists service tests")
public class ArtistTest extends BaseTest {

  private static final Channel artistChannel = ManagedChannelBuilder
      .forAddress(CFG.grpcAddress(), CFG.artistGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  private static final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub
      = RococoArtistServiceGrpc.newBlockingStub(artistChannel);

  private static final String ARTIST_PHOTO_PATH = "img/artists/repin.jpg";

  @Test
  @DisplayName("Getting information about artist from rococo-artist")
  @Artist
  void artistDataShouldBeReturned(ArtistJson artist) {
    String createdArtistId = artist.id().toString();
    ArtistRequest request = ArtistRequest.newBuilder()
        .setId(copyFromUtf8(createdArtistId))
        .build();

    ArtistResponse artistResponse = artistStub.getArtist(request);

    step("Check artist in response", () -> assertEquals(artist, fromGrpcMessage(artistResponse)));
  }

  @Test
  @DisplayName("Verify error in case artist not found by ID")
  void verifyErrorIfArtistNotFoundById() {
    String randomId = UUID.randomUUID().toString();
    ArtistRequest request = ArtistRequest.newBuilder()
        .setId(copyFromUtf8(randomId))
        .build();

    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> artistStub.getArtist(request));

    step("Check that artists was not found",
        () -> assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode()));
    step("Check message for not found artist",
        () -> assertEquals("Artist not found by id: " + randomId,
            exception.getStatus().getDescription()));
  }

  @Test
  @DisplayName("Verify creating new artist")
  void verifyThatArtistCreated() {
    String artistName = randomArtistName();
    String biography = randomDescription();
    ByteString photo = copyFromUtf8(ImageUtils.convertImgToBase64(ARTIST_PHOTO_PATH));

    AddArtistRequest addArtistRequest = AddArtistRequest.newBuilder()
        .setName(artistName)
        .setBiography(biography)
        .setPhoto(photo)
        .build();

    ArtistResponse artistResponse = artistStub.addArtist(addArtistRequest);

    step("Check that response is not null", () -> assertNotNull(artistResponse));
    step("Check that ID generated and not null", () -> assertNotNull(artistResponse.getId()));
    step("Check name of created artist", () -> assertEquals(artistName, artistResponse.getName()));
    step("Check biography of created artist", () -> assertEquals(biography, artistResponse.getBiography()));
    step("Check photo of created artist", () -> assertEquals(photo, artistResponse.getPhoto()));
  }


  @Test
  @Artist
  @DisplayName("Verify editing an existing artist")
  void verifyThatArtistEdited(ArtistJson artist) {
    AddArtistRequest updatedData = AddArtistRequest.newBuilder()
        .setName("Updated " + artist.name())
        .setBiography("Updated " + artist.biography())
        .setPhoto(copyFromUtf8(ImageUtils.convertImgToBase64(ARTIST_PHOTO_PATH)))
        .build();

    EditArtistRequest editRequest = EditArtistRequest.newBuilder()
        .setId(copyFromUtf8(artist.id().toString()))
        .setArtistData(updatedData)
        .build();

    ArtistResponse updatedArtist = artistStub.editArtist(editRequest);

    step("Check that updated artist has new values", () -> {
      assertEquals("Updated " + artist.name(), updatedArtist.getName());
      assertEquals("Updated " + artist.biography(), updatedArtist.getBiography());
    });
  }

  @Test
  @DisplayName("Verify getting multiple artists by IDs")
  void verifyGettingArtistsByIds() {
    AddArtistRequest request1 = AddArtistRequest.newBuilder()
        .setName(randomArtistName())
        .setBiography(randomDescription())
        .setPhoto(copyFromUtf8(ImageUtils.convertImgToBase64(ARTIST_PHOTO_PATH)))
        .build();

    AddArtistRequest request2 = AddArtistRequest.newBuilder()
        .setName(randomArtistName())
        .setBiography(randomDescription())
        .setPhoto(copyFromUtf8(ImageUtils.convertImgToBase64(ARTIST_PHOTO_PATH)))
        .build();

    ArtistResponse artist1 = artistStub.addArtist(request1);
    ArtistResponse artist2 = artistStub.addArtist(request2);

    ArtistIdsRequest idsRequest = ArtistIdsRequest.newBuilder()
        .addId(artist1.getId())
        .addId(artist2.getId())
        .build();

    AllArtistByIdsResponse response = artistStub.getArtistByIds(idsRequest);

    step("Check that response contains both artists", () -> {
      assertEquals(2, response.getArtistList().size());
    });
  }

  @Test
  @DisplayName("Verify paginated retrieval of artists")
  void verifyPaginationOfArtists() {
    for (int i = 0; i < 5; i++) {
      AddArtistRequest request = AddArtistRequest.newBuilder()
          .setName(randomArtistName())
          .setBiography(randomDescription())
          .setPhoto(copyFromUtf8(ImageUtils.convertImgToBase64(ARTIST_PHOTO_PATH)))
          .build();
      artistStub.addArtist(request);
    }

    AllArtistRequest paginatedRequest = AllArtistRequest.newBuilder()
        .setPage(0)
        .setSize(3)
        .build();

    AllArtistsResponse response = artistStub.getAllArtists(paginatedRequest);

    step("Check that response contains up to 3 artists", () -> {
      assertTrue(response.getArtists1List().size() <= 3);
    });
  }
}
