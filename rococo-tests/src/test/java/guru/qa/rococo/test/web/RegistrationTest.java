package guru.qa.rococo.test.web;

import static utils.RandomDataUtils.longUsername;
import static utils.RandomDataUtils.randomUsername;
import static utils.RandomDataUtils.shortUsername;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.UserJson;

@WebTest
@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.WEB)
@Feature(AllureFeature.REGISTRATION)
@Tag(JTag.WEB)
public class RegistrationTest extends BaseTest {

  @Test
  @DisplayName("Verify registration new user")
  void shouldRegisterNewUser() {
    String username = randomUsername();
    String password = "123";

    registerPage
        .open()
        .checkThatPageLoaded()
        .fillRegistrationForm(username, password, password)
        .successSubmit()
        .getHeader()
        .toLoginPage()
        .enterLoginData(username, password)
        .submit(mainPage)
        .checkThatPageLoaded();
  }

  @Test
  @User
  @DisplayName("Already created user cannot be registered")
  void alreadyCreatedUserCannotBeRegistered(UserJson user) {
    registerPage
        .open()
        .checkThatPageLoaded()
        .fillRegistrationForm(user.username(), user.password(), user.password())
        .submit()
        .checkErrorMessage(String.format("Username `%s` already exists", user.username()));
  }

  @Test
  @DisplayName("Verify possible range of username length")
  void checkUsernameRangeLength() {
    String shortUsername = shortUsername();
    String longUsername = longUsername();
    String password = "123";
    String errorMessage = "Allowed username length should be from 3 to 50 characters";

    registerPage
        .open()
        .checkThatPageLoaded()
        .fillRegistrationForm(shortUsername, password, password)
        .submit()
        .checkErrorMessage(errorMessage)
        .clearUsernameInput()
        .fillRegistrationForm(longUsername, password, password)
        .submit()
        .checkErrorMessage(errorMessage);
  }

  @Test
  @DisplayName("Passwords should be equal")
  void passwordShouldBeEqual() {
    String username = randomUsername();
    String password = "123";
    String passwordSubmit = "321";
    String errorMessage = "Passwords should be equal";

    registerPage
        .open()
        .checkThatPageLoaded()
        .fillRegistrationForm(username, password, passwordSubmit)
        .submit()
        .checkErrorMessage(errorMessage);
  }

  @Test
  @DisplayName("Verify possible range of password length")
  void checkPasswordRangeLength() {
    String username = randomUsername();
    String shortPassword = "1";
    String longSubmit = "long_password";
    String errorMessage = "Allowed password length should be from 3 to 12 characters";

    registerPage
        .open()
        .checkThatPageLoaded()
        .fillRegistrationForm(username, shortPassword, shortPassword)
        .submit()
        .checkErrorMessageForEachInput(errorMessage)
        .clearUsernameInput()
        .clearPasswordInput()
        .clearPasswordSubmitInput()
        .fillRegistrationForm(username, longSubmit, longSubmit)
        .checkErrorMessageForEachInput(errorMessage);
  }
}
