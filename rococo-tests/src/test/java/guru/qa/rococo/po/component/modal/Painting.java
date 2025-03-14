package guru.qa.rococo.po.component.modal;

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
  private final SelenideElement updatePaintingBtn = self.$(".variant-filled-primary");

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

  @Step("Select museum {}")
  @Nonnull
  public Painting selectMuseum(String museumTitle) {
    LOGGER.info("Select museum {}", museumTitle);
    museumSelector.find(text(museumTitle)).scrollIntoView(true).click();
    return this;
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

  @Step("Verify error message {0}")
  public Painting verifyErrorMessage(String errorMessage) {
    LOGGER.info("Verify error message {}", errorMessage);
    updatePaintingBtn.shouldBe(text(errorMessage));
    return this;
  }
}
