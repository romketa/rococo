package guru.qa.rococo.po.component.modal;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.po.DetailedPaintingPage;
import guru.qa.rococo.po.component.BaseComponent;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class Painting extends BaseComponent<Painting> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Painting.class);
  private final SelenideElement avatarEl = self.$(".avatar-image");
  private final SelenideElement loadContentInput = self.$("input[type='file']");
  private final SelenideElement titleInput = self.$("input[name='title']");
  private final SelenideElement descriptionInput = self.$("textarea[name='description']");
  private final ElementsCollection museumSelector = self.$$("select[name='museumId'] option");
  private final ElementsCollection authorSelector = self.$$("select[name='authorId'] option");
  private final SelenideElement updatePaintingBtn = self.$(".variant-filled-primary");
  private final ElementsCollection errorMessageEl = self.$$(".text-error-400");

  public Painting() {
    super($(".modal"));
  }

  @Step("Check that Painting modal opened")
  @Nonnull
  public Painting checkThatModalOpened() {
    LOGGER.info("Check that Painting modal opened");
    $(".modal").shouldBe(visible);
    return this;
  }

  @Step("Fill Painting text form with title {0}, description {1}")
  @Nonnull
  public Painting fillTextPaintingForm(String title, String description) {
    LOGGER.info("Fill Painting text form with name {}, biography {}", title, description);
    titleInput.setValue(title);
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Upload content from path {0}")
  @Nonnull
  public Painting uploadContent(String path) {
    LOGGER.info("Upload photo from path {}", path);
    loadContentInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Select museum {0}")
  @Nonnull
  public Painting selectMuseum(String museumTitle) {
    LOGGER.info("Select museum {}", museumTitle);
    // Максимальное количество попыток прокрутки
    int maxAttempts = 50;
    int attempts = 0;

    // Пока элемент не найден и не превышено количество попыток
    while (attempts < maxAttempts) {
      // Проверяем, есть ли элемент с нужным текстом
      if (museumSelector.find(text(museumTitle)).exists()) {
        // Если элемент найден и доступен для взаимодействия, кликаем по нему
        if (museumSelector.find(text(museumTitle)).is(interactable)) {
          museumSelector.find(text(museumTitle)).scrollIntoView(true).click();
          return this;
        }
      }

      // Прокручиваем список вниз
      museumSelector.last().scrollIntoView(true);
      attempts++;
    }

    // Если элемент не найден после всех попыток, выбрасываем исключение
    throw new RuntimeException(
        "Museum with title '" + museumTitle + "' not found or not interactable after " + maxAttempts
            + " attempts");
  }

  @Step("Select author {}")
  @Nonnull
  public Painting selectAuthor(String author) {
    LOGGER.info("Select author {}", author);
    // Максимальное количество попыток прокрутки
    int maxAttempts = 50;
    int attempts = 0;

    // Пока элемент не найден и не превышено количество попыток
    while (attempts < maxAttempts) {
      // Проверяем, есть ли элемент с нужным текстом
      if (authorSelector.find(text(author)).exists()) {
        // Если элемент найден и доступен для взаимодействия, кликаем по нему
        if (authorSelector.find(text(author)).is(interactable)) {
          authorSelector.find(text(author)).scrollIntoView(true).click();
          return this;
        }
      }

      // Прокручиваем список вниз
      authorSelector.last().scrollIntoView(true);
      attempts++;
    }

    // Если элемент не найден после всех попыток, выбрасываем исключение
    throw new RuntimeException(
        "Author with title '" + author + "' not found or not interactable after " + maxAttempts
            + " attempts");
  }

  @Step("Click by button Add painting")
  public <Page> Page addPainting(Page expectedPage) {
    LOGGER.info("Click by button Add artist");
    updatePaintingBtn.click();
    return expectedPage;
  }

  @Step("Click by button Save painting")
  public DetailedPaintingPage savePainting() {
    LOGGER.info("Click by button Save painting");
    updatePaintingBtn.click();
    return new DetailedPaintingPage();
  }

  @Step("Submit error")
  public Painting submitError() {
    LOGGER.info("Submit error");
    updatePaintingBtn.click();
    return this;
  }

  @Step("Verify error message for title {0}")
  public Painting verifyErrorMessageForTitle(String errorMessage) {
    LOGGER.info("Verify error message for title {}", errorMessage);
    errorMessageEl.first().shouldBe(text(errorMessage));
    return this;
  }

  @Step("Verify error message for description {0}")
  public Painting verifyErrorMessageForDescription(String errorMessage) {
    LOGGER.info("Verify error message for title {}", errorMessage);
    errorMessageEl.get(3).shouldBe(visible, text(errorMessage));
    return this;
  }
}
