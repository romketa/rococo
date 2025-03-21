package guru.qa.rococo.test.web;


import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.po.LoginPage;
import guru.qa.rococo.po.MainPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.WEB)
@Feature(AllureFeature.LOGIN)
@Tag(JTag.WEB)
@DisplayName("WEB. Login web tests")
public class LoginTest extends BaseTest {


  @User
  @Test
  @DisplayName("Main page should be displayed after success login")
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    mainPage.open()
        .checkThatPageLoaded()
        .getHeader()
        .toLoginPage()
        .enterLoginData(user.username(), user.password())
        .submit(new MainPage())
        .checkThatPageLoaded();
  }

  @User
  @Test
  @DisplayName("Verify error in case login with incorrect username")
  @Description("User should stay on login page after login with incorrect username")
  void userShouldStayOnLoginPageAfterLoginWithBadUsername(UserJson user) {
    mainPage
        .open()
        .checkThatPageLoaded()
        .getHeader()
        .toLoginPage()
        .enterLoginData(user.username() + "incorrect", user.password())
        .submit(new LoginPage())
        .verifyLoginError();
  }

  @User
  @Test
  @DisplayName("Verify error in case login with incorrect password")
  @Description("User should stay on login page after login with incorrect password")
  void userShouldStayOnLoginPageAfterLoginWithBadPassword(UserJson user) {
    mainPage
        .open()
        .checkThatPageLoaded()
        .getHeader()
        .toLoginPage()
        .enterLoginData(user.username(), user.password() + "incorrect")
        .submit(new LoginPage())
        .verifyLoginError();
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Verify that session is stopped after user sign out")
  @Description("Session is stopped in case sign out")
  void sessionShouldStopAfterUserLogOut() {
    mainPage
        .open()
        .checkThatPageLoaded()
        .getHeader()
        .openProfile()
        .signOut(mainPage)
        .checkAlert("Сессия завершена");
  }
}
