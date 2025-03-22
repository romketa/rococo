package guru.qa.rococo.test.web;

import static utils.RandomDataUtils.extraLongValue;
import static utils.RandomDataUtils.randomArtistName;
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
import guru.qa.rococo.po.DetailedArtistPage;
import guru.qa.rococo.po.DetailedPaintingPage;
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
@Feature(AllureFeature.ARTIST)
@Tag(JTag.WEB)
@DisplayName("WEB: Rococo-artists web tests")
public class ArtistTest extends BaseTest {

  @Test
  @DisplayName("User can move to artists page from main page from main body")
  void userCanNavigateFromMainToArtistByBodyImage() {
    mainPage
        .open()
        .goToArtistsPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("User can move to artists page from main page from header")
  void userCanNavigateFromMainToArtistHeader() {
    mainPage
        .open()
        .getHeader()
        .toArtistsPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Not authorized user cannot add artist")
  void notAuthorizedUserCannotAddArtist() {
    artistsPage
        .open()
        .checkThatPageLoaded()
        .addArtistButtonShouldBeHidden();
  }

  @Test
  @DisplayName("Message 'No Artists found' should be displayed while artists searching")
  void messageNoArtistsFoundShouldBeDisplayedWhileSearchingArtists() {

    artistsPage
        .open()
        .checkThatPageLoaded()
        .filterArtistsByName("123asd")
        .checkMessageNoArtistsFound();
  }

  @Test
  @User
  @ApiLogin
  @ScreenShotTest("img/artists/expected-repin.png")
  @DisplayName("Authorized user can add artist")
  void authorizedUserCanAddArtist(BufferedImage image) throws IOException, InterruptedException {
    String name = randomArtistName();
    String biography = randomDescription();

    artistsPage
        .open()
        .checkThatPageLoaded()
        .addArtist()
        .fillTextArtistForm(name, biography)
        .uploadPhoto("img/artists/repin.jpg")
        .addArtist()
        .checkAlert("Добавлен художник: " + name)
        .filterArtistsByName(name)
        .checkThatArtistCreated(name)
        .openDetailedArtistInfo(name)
        .checkArtistName(name)
        .checkArtistBiography(biography)
        .checkArtistPhoto(image);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of artist name length")
  void verifyPossibleRangeOfArtistNameLength() {
    String name = randomLongValue();
    String biography = randomDescription();

    artistsPage
        .open()
        .checkThatPageLoaded()
        .addArtist()
        .fillTextArtistForm(name, biography)
        .uploadPhoto("img/artists/repin.jpg")
        .submitError()
        .verifyErrorMessageForArtistName("Имя не может быть длиннее 255 символов")
        .fillTextArtistForm("a", biography)
        .submitError()
        .verifyErrorMessageForArtistName("Имя не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of artist biography length")
  void verifyPossibleRangeOfArtistBiographyLength() {
    String name = randomArtistName();
    String biography = extraLongValue();

    artistsPage
        .open()
        .checkThatPageLoaded()
        .addArtist()
        .fillTextArtistForm(name, biography)
        .uploadPhoto("img/artists/repin.jpg")
        .submitError()
        .verifyErrorMessageForArtistBiography("Биография не может быть длиннее 2000 символов")
        .fillTextArtistForm(name, "a")
        .submitError()
        .verifyErrorMessageForArtistBiography("Биография не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @Artist
  @ScreenShotTest("img/artists/expected-dali.png")
  @DisplayName("User can modify already created artist")
  void userCanModifyArtist(ArtistJson artist, BufferedImage image)
      throws IOException, InterruptedException {
    String artistName = randomArtistName();
    String artistBio = randomDescription();

    artistsPage
        .open()
        .filterArtistsByName(artist.name())
        .openDetailedArtistInfo(artist.name())
        .editArtist()
        .fillTextArtistForm(artistName, artistBio)
        .uploadPhoto("img/artists/dali.jpg")
        .saveArtist()
        .checkAlert("Обновлен художник: " + artistName)
        .checkArtistName(artistName)
        .checkArtistBiography(artistBio)
        .checkArtistPhoto(image);
  }

  @Test
  @User
  @ApiLogin
  @Artist
  @Museum
  @ScreenShotTest("img/paintings/expected-burlaks.png")
  @DisplayName("User can add painting for artist")
  void userCanAddPaintingForArtist(ArtistJson artist, MuseumJson museum, BufferedImage image)
      throws IOException, InterruptedException {
    String paintingTitle = randomPaintingTitle();
    String description = randomDescription();

    artistsPage
        .open()
        .filterArtistsByName(artist.name())
        .openDetailedArtistInfo(artist.name())
        .addFirstPainting()
        .checkThatModalOpened()
        .fillTextPaintingForm(paintingTitle, description)
        .uploadContent("img/paintings/burlaks.jpg")
        .selectMuseum(museum.title())
        .addPainting(new DetailedArtistPage())
        .checkAlert("Добавлена картина: " + paintingTitle)
        .checkThatPaintingAddedForArtist(paintingTitle, image);
  }

  @Test
  @User
  @ApiLogin
  @Museum
  @Artist
  @Painting
  @ScreenShotTest("img/paintings/expected-burlaks.png")
  @DisplayName("User can add painting for artist with already added painting")
  void addPaintingForArtistWithPainting(ArtistJson artist, MuseumJson museum, BufferedImage image)
      throws IOException, InterruptedException {
    String paintingTitle = randomPaintingTitle();
    String description = randomDescription();

    artistsPage
        .open()
        .filterArtistsByName(artist.name())
        .openDetailedArtistInfo(artist.name())
        .addPainting()
        .fillTextPaintingForm(paintingTitle, description)
        .uploadContent("img/paintings/burlaks.jpg")
        .selectMuseum(museum.title())
        .addPainting(new DetailedArtistPage())
        .checkAlert("Добавлена картина: " + paintingTitle)
        .checkThatPaintingAddedForArtist(paintingTitle, image);
  }
}
