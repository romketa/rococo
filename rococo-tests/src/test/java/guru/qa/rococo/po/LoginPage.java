package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.po.component.Header;

public class LoginPage extends BasePage<LoginPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginPage.class);
  public static final String URL = Config.getInstance().authUrl() + "login";
  private final SelenideElement title = $("h1");
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement loginBtn = $("button[type='submit']");
  private final SelenideElement errorMessage = $(".login__error");
  protected final Header header = new Header();

  @Override
  @Nonnull
  @Step("Check that login page loaded")
  public LoginPage checkThatPageLoaded() {
    LOGGER.info("Check that login page loaded");
    title.shouldBe(visible);
    return this;
  }

  @Nonnull
  @Step("Successful login by {username} and {password}")
  public LoginPage enterLoginData(String username, String password) {
    LOGGER.info("Successful login by {} and {}", username, password);
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Step("Submit login")
  @Nonnull
  public <T extends BasePage<?>> T submit(T expectedPage) {
    LOGGER.info("Submit login");
    loginBtn.click();
    return expectedPage;
  }

  @Nonnull
  @Step("Verify error by logging with incorrect credentials")
  public LoginPage verifyLoginError() {
    LOGGER.info("Verify error by logging with incorrect credentials");
    errorMessage.shouldBe(exactText("Неверные учетные данные пользователя"));
    return this;
  }
}
