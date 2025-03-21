package guru.qa.rococo.test.web;

import static utils.RandomDataUtils.randomLastName;
import static utils.RandomDataUtils.randomName;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.UserJson;
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
@Feature(AllureFeature.PROFILE)
@Tag(JTag.WEB)
@DisplayName("WEB. Profile web tests")
public class ProfileTest extends BaseTest {

  @User
  @ApiLogin
  @Test
  @ScreenShotTest("img/ava/expected-spider-man.png")
  @DisplayName("Empty user should be filled")
  void shouldFillEmptyUser(BufferedImage expectedAvatar) throws IOException, InterruptedException {
    String firstName = randomName();
    String lastName = randomLastName();

    mainPage
        .getHeader()
        .openProfile()
        .fillTextProfileForm(firstName, lastName)
        .uploadAvatar("img/ava/spider-man-ava.jpg")
        .updateProfile();
    mainPage
        .checkAlert("Профиль обновлен")
        .getHeader()
        .openProfile()
        .checkThatModalOpened()
        .checkNames(firstName, lastName)
        .checkPhotoExist()
        .checkAvatarImg(expectedAvatar);
  }

  @User
  @ApiLogin
  @Test
  @ScreenShotTest("img/ava/expected-spider-man.png")
  @DisplayName("Update not empty user")
  void shouldUpdateNotEmptyUser(BufferedImage expectedAvatar)
      throws IOException, InterruptedException {
    String firstName = randomName();
    String lastName = randomLastName();
    String updatedName = randomName();
    String updatedLastName = randomLastName();

    mainPage
        .getHeader()
        .openProfile()
        .fillTextProfileForm(firstName, lastName)
        .uploadAvatar("img/ava/venom-ava.jpg")
        .updateProfile();
    mainPage
        .checkAlert("Профиль обновлен")
        .getHeader()
        .openProfile()
        .checkThatModalOpened()
        .fillTextProfileForm(updatedName, updatedLastName)
        .uploadAvatar("img/ava/spider-man-ava.jpg")
        .updateProfile()
        .checkThatModalClosed();
    mainPage
        .checkAlert("Профиль обновлен")
        .getHeader()
        .openProfile()
        .checkThatModalOpened()
        .checkPhotoExist()
        .checkAvatarImg(expectedAvatar);
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Check username in profile")
  void usernameShouldBeDisplayedInProfile(UserJson user) {
    mainPage
        .getHeader()
        .openProfile()
        .checkUsername(user.username());
  }
}
