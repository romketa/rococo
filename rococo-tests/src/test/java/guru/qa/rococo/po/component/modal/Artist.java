package guru.qa.rococo.po.component.modal;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.po.ArtistsPage;
import guru.qa.rococo.po.DetailedArtistPage;
import guru.qa.rococo.po.component.BaseComponent;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class Artist extends BaseComponent<Artist> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Artist.class);
  private final SelenideElement loadPhotoInput = self.$("input[type='file']");
  private final SelenideElement nameInput = self.$("input[name='name']");
  private final SelenideElement biographyInput = self.$("textarea[name='biography']");
  private final SelenideElement updateArtistBtn = self.$(".variant-filled-primary");
  private final SelenideElement errorMessageEl = self.$(".text-error-400");

  public Artist() {
    super($(".modal"));
  }

  @Step("Check that Artist modal opened")
  @Nonnull
  public Artist checkThatModalOpened() {
    LOGGER.info("Check that Artist modal opened");
    $(".modal").shouldBe(visible);
    return this;
  }

  @Step("Fill Artist form with name {0}, biography {1}")
  @Nonnull
  public Artist fillTextArtistForm(String name, String biography) {
    LOGGER.info("Fill Artist form with name {}, biography {}", name, biography);
    nameInput.setValue(name);
    biographyInput.setValue(biography);
    return this;
  }

  @Step("Upload photo from path {0}")
  @Nonnull
  public Artist uploadPhoto(String path) {
    LOGGER.info("Upload photo from path {}", path);
    loadPhotoInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Submit error")
  public Artist submitError() {
    LOGGER.info("Submit error");
    updateArtistBtn.click();
    return this;
  }

  @Step("Click by button Add artist")
  public ArtistsPage addArtist() {
    LOGGER.info("Click by button Add artist");
    updateArtistBtn.click();
    return new ArtistsPage();
  }

  @Step("Click by button save artist")
  public DetailedArtistPage saveArtist() {
    LOGGER.info("Click by button save artist");
    updateArtistBtn.click();
    return new DetailedArtistPage();
  }

  @Step("Verify error message {0}")
  public Artist verifyErrorMessage(String errorMessage) {
    LOGGER.info("Verify error message {}", errorMessage);
    errorMessageEl.shouldBe(text(errorMessage));
    return this;
  }
}
