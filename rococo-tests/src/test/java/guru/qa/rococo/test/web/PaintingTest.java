package guru.qa.rococo.test.web;

import static utils.RandomDataUtils.extraLongValue;
import static utils.RandomDataUtils.randomDescription;
import static utils.RandomDataUtils.randomLongValue;
import static utils.RandomDataUtils.randomPaintingTitle;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.po.PaintingsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.WEB)
@Feature(AllureFeature.PAINTING)
@Tag(JTag.WEB)
@DisplayName("WEB: Rococo-paintings web tests")
public class PaintingTest extends BaseTest {

  @Test
  @DisplayName("User can move to paintings page from main page from main body")
  void userCanNavigateFromMainToPaintingByBodyImage() {
    mainPage
        .open()
        .goToPaintingsPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("User can move to paintings page from main page from header")
  void userCanNavigateFromMainToPaintingHeader() {
    mainPage
        .open()
        .getHeader()
        .toPaintingsPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Not authorized user cannot add painting")
  void notAuthorizedUserCannotAddMuseum() {
    paintingsPage
        .open()
        .checkThatPageLoaded()
        .addPaintingButtonShouldBeHidden();
  }

  @Test
  @DisplayName("Message 'No Paintings found' should be displayed while paintings searching")
  void messageNoPaintingsFoundShouldBeDisplayedWhileSearchingPaintings() {

    paintingsPage
        .open()
        .checkThatPageLoaded()
        .filterPaintingsByTitle("asddsada123")
        .checkMessageNoPaintingsFound();
  }

  @Test
  @User
  @ApiLogin
  @Artist
  @Museum
  @ScreenShotTest("img/paintings/expected-burlaks-in-painting-detailed.png")
  @DisplayName("Authorized user can add painting")
  void authorizedUserCanAddPainting(ArtistJson artist, MuseumJson museum, BufferedImage image) throws IOException, InterruptedException {
    String title = randomPaintingTitle();
    String description = randomDescription();

    paintingsPage
        .open()
        .checkThatPageLoaded()
        .addPainting()
        .fillTextPaintingForm(title, description)
        .uploadContent("img/paintings/burlaks.jpg")
        .selectMuseum(museum.title())
        .selectAuthor(artist.name())
        .addPainting(new PaintingsPage())
        .checkAlert("Добавлена картины: " + title)
        .filterPaintingsByTitle(title)
        .checkThatPaintingCreated(title)
        .openDetailedPaintingInfo(title)
        .checkPaintingTitle(title)
        .checkPaintingArtist(artist.name())
        .checkPaintingDescription(description)
        .checkPaintingPhoto(image);
  }

  @Test
  @User
  @ApiLogin
  @Artist
  @Museum
  @Painting
  @ScreenShotTest("img/paintings/expected-burlaks-in-painting-detailed.png")
  @DisplayName("User can edit painting")
  void userCanEditPainting(PaintingJson painting, ArtistJson artist, MuseumJson museum, BufferedImage image) throws IOException, InterruptedException {
    String title = randomPaintingTitle();
    String description = randomDescription();

    paintingsPage
        .open()
        .checkThatPageLoaded()
        .filterPaintingsByTitle(painting.title())
        .openDetailedPaintingInfo(painting.title())
        .checkThatPageLoaded()
        .editPainting()
        .checkThatModalOpened()
        .fillTextPaintingForm(title, description)
        .uploadContent("img/paintings/burlaks.jpg")
        .selectMuseum(museum.title())
        .selectAuthor(artist.name())
        .savePainting()
        .checkAlert("Обновлена картина: " + title)
        .checkThatPaintingUpdated(title, artist.name(), description, image);
  }

  @Test
  @User
  @Artist
  @Museum
  @ApiLogin
  @DisplayName("Verify possible range of painting title length")
  void verifyPossibleRangeOfPaintingTitleLength(ArtistJson artist, MuseumJson museum) {
    String title = randomLongValue();
    String description = randomDescription();

    paintingsPage
        .open()
        .checkThatPageLoaded()
        .addPainting()
        .fillTextPaintingForm(title, description)
        .uploadContent("img/paintings/burlaks.jpg")
        .selectMuseum(museum.title())
        .selectAuthor(artist.name())
        .submitError()
        .verifyErrorMessageForTitle("Название не может быть длиннее 255 символов")
        .fillTextPaintingForm("a", description)
        .submitError()
        .verifyErrorMessageForTitle("Название не может быть короче 3 символов");
  }

  @Test
  @User
  @Artist
  @Museum
  @ApiLogin
  @DisplayName("Verify possible range of painting description length")
  void verifyPossibleRangeOfPaintingDescriptionLength(ArtistJson artist, MuseumJson museum) {
    String title = randomPaintingTitle();
    String description = extraLongValue();

    paintingsPage
        .open()
        .checkThatPageLoaded()
        .addPainting()
        .fillTextPaintingForm(title, description)
        .uploadContent("img/paintings/burlaks.jpg")
        .selectMuseum(museum.title())
        .selectAuthor(artist.name())
        .submitError()
        .verifyErrorMessageForDescription("Описание не может быть длиннее 2000 символов")
        .fillTextPaintingForm(title, "a")
        .submitError()
        .verifyErrorMessageForDescription("Описание не может быть короче 10 символов");
  }
}
