package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import guru.qa.rococo.config.Config;

public class RegisterPage extends BasePage<RegisterPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterPage.class);
  private static final Config CFG = Config.getInstance();
  private final SelenideElement title = $(".form__header");
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
  private final SelenideElement registrationBtn = $("button[type='submit']");
  private final SelenideElement proceedLoginBtn = $(".form__submit");
  private final SelenideElement goToMainMenuLink = $(".form__link");
  private final ElementsCollection errorMessageEl = $$(".form__error");

  @Step("Open register page")
  @Nonnull
  public RegisterPage open() {
    LOGGER.info("Open register page");
    Selenide.open(CFG.registerUrl());
    return this;
  }

  @Override
  @Step("Check that register page loaded")
  @Nonnull
  public RegisterPage checkThatPageLoaded() {
    LOGGER.info("Check that register page loaded");
    title.shouldBe(visible);
    return this;
  }

  @Step("Fill registration form: username: {0}, password: {1}, submit password: {2}")
  @Nonnull
  public RegisterPage fillRegistrationForm(String username, String password,
      String passwordSubmit) {
    LOGGER.info("Fill registration form: username: {}, password: {}, submit password: {}", username,
        password, passwordSubmit);
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(passwordSubmit);
    return this;
  }

  @Step("Submit register and proceed to login page")
  @Nonnull
  public MainPage successSubmit() {
    LOGGER.info("Submit register and proceed to login page");
    registrationBtn.click();
    proceedLoginBtn.click();
    return new MainPage();
  }

  @Step("Submit register")
  @Nonnull
  public RegisterPage submit() {
    LOGGER.info("Submit register");
    registrationBtn.click();
    return this;
  }

  @Step("Check error message {0}")
  @Nonnull
  public RegisterPage checkErrorMessage(String errorMessage) {
    LOGGER.info("Check error message {}", errorMessage);
    errorMessageEl.first().shouldBe(exactText(errorMessage));
    return this;
  }

  @Step("Check error messages {0}")
  @Nonnull
  public RegisterPage checkErrorMessageForEachInput(String errorMessage) {
    LOGGER.info("Check error messages {}", errorMessage);
    errorMessageEl.forEach(el -> el.shouldBe(exactText(errorMessage)));
    return this;
  }

  @Step("Clear username input")
  @Nonnull
  public RegisterPage clearUsernameInput() {
    LOGGER.info("Clear username input");
    usernameInput.clear();
    return this;
  }

  @Step("Clear password input")
  @Nonnull
  public RegisterPage clearPasswordInput() {
    LOGGER.info("Clear password input");
    passwordInput.clear();
    return this;
  }

  @Step("Clear password submit input")
  @Nonnull
  public RegisterPage clearPasswordSubmitInput() {
    LOGGER.info("Clear password submit input");
    passwordSubmitInput.clear();
    return this;
  }
}
