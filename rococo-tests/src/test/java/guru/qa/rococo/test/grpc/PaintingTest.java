package guru.qa.rococo.test.grpc;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.RandomDataUtils.randomDescription;
import static utils.RandomDataUtils.randomMuseumTitle;
import static utils.RandomDataUtils.randomPaintingTitle;
import static utils.Utils.toByteStringFromUuid;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AddPaintingRequest;
import guru.qa.grpc.rococo.AllPaintingsResponse;
import guru.qa.grpc.rococo.ArtistId;
import guru.qa.grpc.rococo.EditPaintingRequest;
import guru.qa.grpc.rococo.MuseumId;
import guru.qa.grpc.rococo.MuseumRequest;
import guru.qa.grpc.rococo.PaintingByArtistRequest;
import guru.qa.grpc.rococo.PaintingRequest;
import guru.qa.grpc.rococo.PaintingResponse;
import guru.qa.grpc.rococo.RococoPaintingServiceGrpc;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
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
import utils.Utils;

@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.GRPC)
@Feature(AllureFeature.PAINTING)
@Tag(JTag.GRPC)
@DisplayName("GRPC: Rococo-painting service tests")
public class PaintingTest extends BaseTest {

  private static final Channel paintingChannel = ManagedChannelBuilder
      .forAddress(CFG.grpcAddress(), CFG.paintingGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();


  private static final RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub artistStub
      = RococoPaintingServiceGrpc.newBlockingStub(paintingChannel);

  private static final String PAINTING_PHOTO_PATH = "img/paintings/burlaks.jpg";

  @Test
  @Museum
  @Artist
  @DisplayName("Verify adding a new painting")
  void verifyAddingPainting(MuseumJson museum, ArtistJson artist) {
    String title = randomPaintingTitle();
    String description = randomDescription();
    ByteString content = copyFromUtf8(ImageUtils.convertImgToBase64(PAINTING_PHOTO_PATH));

    AddPaintingRequest addRequest = AddPaintingRequest.newBuilder()
        .setTitle(title)
        .setDescription(description)
        .setContent(content)
        .setArtistId(getArtistId(artist))
        .setMuseumId(getMuseumId(museum))
        .build();

    PaintingResponse createdPainting = artistStub.addPainting(addRequest);

    step("Check that created painting has correct values", () -> {
      assertEquals(title, createdPainting.getTitle());
      assertEquals(description, createdPainting.getDescription());
      assertEquals(content, createdPainting.getContent());
    });
  }

  @Test
  @DisplayName("Verify error in case country not found by ID")
  void verifyErrorIfCountryNotFoundById() {
    String randomId = UUID.randomUUID().toString();
    PaintingRequest request = PaintingRequest.newBuilder()
        .setId(copyFromUtf8(randomId))
        .build();

    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> artistStub.getPainting(request));

    step("Check that painting was not found",
        () -> assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode()));
    step("Check message for not found artist",
        () -> assertEquals("Painting not found by id: " + randomId,
            exception.getStatus().getDescription()));
  }

  @Test
  @Artist
  @Museum
  @Painting
  @DisplayName("Verify editing an existing painting")
  void verifyEditingPainting(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
    String title = randomMuseumTitle();
    String description = randomDescription();
    ByteString content = copyFromUtf8(PAINTING_PHOTO_PATH);
    AddPaintingRequest addPaintingRequest = AddPaintingRequest.newBuilder()
        .setTitle(title)
        .setDescription(description)
        .setContent(content)
        .setArtistId(getArtistId(artist))
        .setMuseumId(getMuseumId(museum))
        .build();
    EditPaintingRequest editRequest = EditPaintingRequest.newBuilder()
        .setId(toByteStringFromUuid(painting.id()))
        .setPaintingData(addPaintingRequest)
        .build();

    PaintingResponse updatedPainting = artistStub.editPainting(editRequest);

    step("Check that updated painting has new values", () -> {
      assertEquals(title, updatedPainting.getTitle());
      assertEquals(description, updatedPainting.getDescription());
      assertEquals(content, updatedPainting.getContent());
      assertEquals(getArtistId(artist), updatedPainting.getArtistId());
      assertEquals(getMuseumId(museum), updatedPainting.getMuseumId());
    });
  }

  @Test
  @Artist
  @Museum
  @Painting
  @DisplayName("Verify getting painting by ID")
  void verifyGettingPaintingById(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
    PaintingRequest getRequest = PaintingRequest.newBuilder()
        .setId(toByteStringFromUuid(painting.id()))
        .build();

    PaintingResponse fetchedPainting = artistStub.getPainting(getRequest);

    step("Check that fetched painting matches the created one", () -> {
      assertEquals(toByteStringFromUuid(painting.id()), fetchedPainting.getId());
      assertEquals(painting.title(), fetchedPainting.getTitle());
      assertEquals(painting.description(), fetchedPainting.getDescription());
      assertEquals(copyFromUtf8(painting.content()), fetchedPainting.getContent());
      assertEquals(getArtistId(artist), fetchedPainting.getArtistId());
      assertEquals(getMuseumId(museum), fetchedPainting.getMuseumId());
    });
  }

  @Test
  @Artist
  @Museum
  @DisplayName("Verify getting paintings by artist ID")
  void verifyGettingPaintingsByArtistId(ArtistJson artist, MuseumJson museum) {
    for (int i = 0; i < 3; i++) {
      AddPaintingRequest request = AddPaintingRequest.newBuilder()
          .setTitle(randomPaintingTitle())
          .setDescription(randomDescription())
          .setContent(copyFromUtf8(ImageUtils.convertImgToBase64(PAINTING_PHOTO_PATH)))
          .setArtistId(getArtistId(artist))
          .setMuseumId(getMuseumId(museum))
          .build();
      artistStub.addPainting(request);
    }

    PaintingByArtistRequest artistRequest = PaintingByArtistRequest.newBuilder()
        .setArtistId(toByteStringFromUuid(artist.id()))
        .setPage(0)
        .setSize(3)
        .build();

    AllPaintingsResponse response = artistStub.getPaintingByArtist(artistRequest);

    step("Check that response contains all paintings by artist", () -> {
      assertEquals(3, response.getPaintings1List().size());
    });
  }


  @Nonnull
  private MuseumId getMuseumId(MuseumJson museum) {
    return MuseumId.newBuilder()
        .setId(toByteStringFromUuid(museum.id()))
        .build();
  }

  @Nonnull
  private ArtistId getArtistId(ArtistJson artist) {
    return ArtistId.newBuilder()
        .setId(toByteStringFromUuid(artist.id()))
        .build();
  }
}
