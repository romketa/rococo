package guru.qa.rococo.po.component.modal;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.po.DetailedMuseumPage;
import guru.qa.rococo.po.MuseumsPage;
import guru.qa.rococo.po.component.BaseComponent;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class Museum extends BaseComponent<Museum> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Museum.class);
  private final SelenideElement avatarEl = self.$(".avatar-image");
  private final SelenideElement loadPhotoInput = self.$("input[type='file']");
  private final SelenideElement titleInput = self.$("input[name='title']");
  private final SelenideElement cityInput = self.$("input[name='city']");
  private final SelenideElement descriptionInput = self.$("textarea[name='description']");
  private final ElementsCollection countriesSelector = self.$$("select[name='countryId'] option");
  private final SelenideElement updateMuseumBtn = self.$(".variant-filled-primary");
  private final ElementsCollection errorMessageEl = self.$$(".text-error-400");

  public Museum() {
    super($(".modal"));
  }

  @Step("Check that Museum modal opened")
  @Nonnull
  public Museum checkThatModalOpened() {
    LOGGER.info("Check that Museum modal opened");
    $(".modal").shouldBe(visible);
    return this;
  }

  @Step("Fill Museum text form with title {0}, description {1} and city {2}")
  @Nonnull
  public Museum fillTextMuseumForm(String title, String description, String city) {
    LOGGER.info("Fill Museum text form with name {}, biography {} and city {}", title, description,
        city);
    titleInput.setValue(title);
    descriptionInput.setValue(description);
    cityInput.setValue(city);
    return this;
  }

  @Step("Upload photo from path {0}")
  @Nonnull
  public Museum uploadPhoto(String path) {
    LOGGER.info("Upload photo from path {}", path);
    loadPhotoInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Select country {}")
  @Nonnull
  public Museum selectCountry(String country) {
    LOGGER.info("Select country {}", country);
    // Максимальное количество попыток прокрутки
    int maxAttempts = 50;
    int attempts = 0;

    // Пока элемент не найден и не превышено количество попыток
    while (attempts < maxAttempts) {
      // Проверяем, есть ли элемент с нужным текстом
      if (countriesSelector.find(text(country)).exists()) {
        // Если элемент найден и доступен для взаимодействия, кликаем по нему
        if (countriesSelector.find(text(country)).is(interactable)) {
          countriesSelector.find(text(country)).scrollIntoView(true).click();
          return this;
        }
      }

      // Прокручиваем список вниз
      countriesSelector.last().scrollIntoView(true);
      attempts++;
    }

    // Если элемент не найден после всех попыток, выбрасываем исключение
    throw new RuntimeException(
        "Country '" + country + "' not found or not interactable after " + maxAttempts
            + " attempts");
  }

  @Step("Click by button Add museum")
  public MuseumsPage addMuseum() {
    LOGGER.info("Click by button Add museum");
    updateMuseumBtn.click();
    return new MuseumsPage();
  }

  @Step("Click by button Save Museum")
  public DetailedMuseumPage saveMuseum() {
    LOGGER.info("Click by button Save Museum");
    updateMuseumBtn.click();
    return new DetailedMuseumPage();
  }

  @Step("Submit error")
  public Museum submitError() {
    LOGGER.info("Submit error");
    updateMuseumBtn.click();
    return this;
  }

  @Step("Verify error message for title input {0}")
  public Museum verifyErrorMessageForTitle(String errorMessage) {
    LOGGER.info("Verify error message for title input {}", errorMessage);
    errorMessageEl.first().shouldBe(text(errorMessage));
    return this;
  }

  @Step("Verify error message for title input {0}")
  public Museum verifyErrorMessageForCity(String errorMessage) {
    LOGGER.info("Verify error message for city input{}", errorMessage);
    errorMessageEl.get(2).shouldBe(text(errorMessage));
    return this;
  }

  @Step("Verify error message for title input {0}")
  public Museum verifyErrorMessageForDescription(String errorMessage) {
    LOGGER.info("Verify error message description input {}", errorMessage);
    errorMessageEl.get(4).shouldBe(text(errorMessage));
    return this;
  }
}
