package guru.qa.rococo.test.web;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
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

import static utils.RandomDataUtils.getRandomCountry;
import static utils.RandomDataUtils.randomBiography;
import static utils.RandomDataUtils.randomCity;
import static utils.RandomDataUtils.randomLongValue;
import static utils.RandomDataUtils.randomMuseumTitle;

@WebTest
@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.WEB)
@Feature(AllureFeature.MUSEUM)
@Tag(JTag.WEB)
public class MuseumTest extends BaseTest{

  @Test
  @DisplayName("User can move to museums page from main page from main body")
  void userCanNavigateFromMainToMuseumByBodyImage() {
    mainPage
        .open()
        .goToMuseumsPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("User can move to museums page from main page from header")
  void userCanNavigateFromMainToMuseumHeader() {
    mainPage
        .open()
        .getHeader()
        .toMuseumsPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Not authorized user cannot add museum")
  void notAuthorizedUserCannotAddMuseum() {
    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseumButtonShouldBeHidden();
  }

  @Test
  @User
  @ApiLogin
  @ScreenShotTest("img/artists/expected-repin.png")
  @DisplayName("Authorized user can add Museum")
  void authorizedUserCanAddMuseum(BufferedImage image) throws IOException, InterruptedException {
    String title = randomMuseumTitle();
    String biography = randomBiography();
    String city = randomCity();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, biography, city)
        .uploadPhoto("img/museums/luvr.jpg")
        .addMuseum()
        .checkAlert("Добавлен музей: " + title)
        .filterMuseumsByTitle(title)
        .checkThatMuseumCreated(title)
        .openDetailedMuseumInfo(title)
        .checkMuseumTitle(title)
        .checkMuseumDescription(biography)
        .checkMuseumPhoto(image);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of museum title length")
  void verifyPossibleRangeOfMuseumTitleLength() {
    String name = randomLongValue();
    String biography = randomBiography();
    String city = randomCity();
    String country = getRandomCountry().name();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(name, biography, city)
        .selectCountry(country)
        .uploadPhoto("img/museums/luvr.jpg")
        .submitError()
        .verifyErrorMessageForTitle("Название не может быть длиннее 255 символов")
        .fillTextMuseumForm("a", biography, city)
        .submitError()
        .verifyErrorMessageForTitle("Название не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of museum city length")
  void verifyPossibleRangeOfMuseumCityLength() {
    String city = randomLongValue();
    String biography = randomBiography();
    String title = randomMuseumTitle();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, biography, city)
        .uploadPhoto("img/museums/luvr.jpg")
        .submitError()
        .verifyErrorMessageForCity("Город не может быть длиннее 255 символов")
        .fillTextMuseumForm("a", biography, city)
        .submitError()
        .verifyErrorMessageForCity("Город не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of museum description length")
  void verifyPossibleRangeOfMuseumDescriptionLength() {
    String title = randomLongValue();
    String biography = randomBiography();
    String city = randomCity();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, biography, city)
        .uploadPhoto("img/museums/luvr.jpg")
        .submitError()
        .verifyErrorMessageForDescription("Описание не может быть короче 10 символов")
        .fillTextMuseumForm("a", biography, city)
        .submitError()
        .verifyErrorMessageForDescription("2000");
  }
}
