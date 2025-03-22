package guru.qa.rococo.test.web;

import static utils.RandomDataUtils.extraLongValue;
import static utils.RandomDataUtils.getRandomCountry;
import static utils.RandomDataUtils.randomCity;
import static utils.RandomDataUtils.randomDescription;
import static utils.RandomDataUtils.randomLongValue;
import static utils.RandomDataUtils.randomMuseumTitle;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.MuseumJson;
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
@Feature(AllureFeature.MUSEUM)
@Tag(JTag.WEB)
@DisplayName("WEB: Rococo-museums web tests")
public class MuseumTest extends BaseTest {

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
  @DisplayName("Message 'No Museums found' should be displayed while museums searching")
  void messageNoMuseumsFoundShouldBeDisplayedWhileSearchingMuseums() {

    museumsPage
        .open()
        .checkThatPageLoaded()
        .filterMuseumsByTitle("asd")
        .checkMessageNoMuseumsFound();
  }

  @Test
  @User
  @ApiLogin
  @ScreenShotTest("img/museums/expected-luvr.png")
  @DisplayName("Authorized user can add museum")
  void authorizedUserCanAddMuseum(BufferedImage image) throws IOException, InterruptedException {
    String title = randomMuseumTitle();
    String description = randomDescription();
    String city = randomCity();
    String country = getRandomCountry().name();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, description, city)
        .uploadPhoto("img/museums/luvr.jpg")
        .selectCountry(country)
        .addMuseum()
        .checkAlert("Добавлен музей: " + title)
        .filterMuseumsByTitle(title)
        .checkThatMuseumCreated(title)
        .openDetailedMuseumInfo(title)
        .checkMuseumTitle(title)
        .checkMuseumDescription(description)
        .checkMuseumPhoto(image);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of museum title length")
  void verifyPossibleRangeOfMuseumTitleLength() {
    String title = randomLongValue();
    String description = randomDescription();
    String city = randomCity();
    String country = getRandomCountry().name();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, description, city)
        .selectCountry(country)
        .uploadPhoto("img/museums/luvr.jpg")
        .submitError()
        .verifyErrorMessageForTitle("Название не может быть длиннее 255 символов")
        .fillTextMuseumForm("a", description, city)
        .submitError()
        .verifyErrorMessageForTitle("Название не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of museum city length")
  void verifyPossibleRangeOfMuseumCityLength() {
    String city = randomLongValue();
    String description = randomDescription();
    String title = randomMuseumTitle();
    String country = getRandomCountry().name();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, description, city)
        .selectCountry(country)
        .uploadPhoto("img/museums/luvr.jpg")
        .submitError()
        .verifyErrorMessageForCity("Город не может быть длиннее 255 символов")
        .fillTextMuseumForm(title, description, "a")
        .submitError()
        .verifyErrorMessageForCity("Город не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Verify possible range of museum description length")
  void verifyPossibleRangeOfMuseumDescriptionLength() {
    String title = randomMuseumTitle();
    String description = extraLongValue();
    String city = randomCity();
    String country = getRandomCountry().name();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .addMuseum()
        .fillTextMuseumForm(title, description, city)
        .uploadPhoto("img/museums/luvr.jpg")
        .selectCountry(country)
        .submitError()
        .verifyErrorMessageForDescription("Описание не может быть длиннее 2000 символов")
        .fillTextMuseumForm(title, "a", city)
        .submitError()
        .verifyErrorMessageForDescription("Описание не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @Museum
  @ScreenShotTest("img/museums/expected-luvr.png")
  @DisplayName("User can modify museum")
  void userCanModifyMuseum(MuseumJson museum, BufferedImage image)
      throws IOException, InterruptedException {
    String title = randomMuseumTitle();
    String description = randomDescription();
    String city = randomCity();
    String country = getRandomCountry().name();

    museumsPage
        .open()
        .checkThatPageLoaded()
        .filterMuseumsByTitle(museum.title())
        .openDetailedMuseumInfo(museum.title())
        .editMuseum()
        .fillTextMuseumForm(title, description, city)
        .uploadPhoto("img/museums/luvr.jpg")
        .selectCountry(country)
        .saveMuseum()
        .checkAlert("Обновлен музей: " + title)
        .checkThatMuseumUpdated(title, city, country, description, image);
  }
}
