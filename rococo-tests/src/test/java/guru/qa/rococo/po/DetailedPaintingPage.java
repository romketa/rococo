package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.po.component.modal.Painting;
import guru.qa.rococo.po.component.modal.Painting;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScreenDiffResult;

public class DetailedPaintingPage extends BasePage<DetailedPaintingPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DetailedPaintingPage.class);
  private final SelenideElement page = $("#page-content");
  private final SelenideElement paintingTitle = page.$(".card-header");
  private final SelenideElement artistPainting = page.$$(".text-center").get(1);
  private final SelenideElement paintingDescription = page.$$(".m-4").get(1);
  private final SelenideElement editPaintingBtn = page.$("button[data-testid='edit-painting']");
  private final SelenideElement paintingContent = page.$("img");

  @Step("Check that Painting detailed page loaded")
  @Nonnull
  @Override
  public DetailedPaintingPage checkThatPageLoaded() {
    LOGGER.info("Check that Painting detailed page loaded");
    paintingTitle.shouldBe(visible);
    return this;
  }

  @Step("Edit painting")
  @Nonnull
  public Painting editPainting() {
    LOGGER.info("Edit painting");
    editPaintingBtn.shouldBe(enabled).click();
    return new Painting();
  }

  @Step("Verify that edit painting is not allowed")
  @Nonnull
  public DetailedPaintingPage verifyThatEditPaintingIsNotAllowed() {
    LOGGER.info("Verify that edit painting is not allowed");
    editPaintingBtn.shouldBe(not(exist));
    return this;
  }

  @Step("Check Painting photo")
  @Nonnull
  public DetailedPaintingPage checkPaintingPhoto(BufferedImage expectedPhoto)
      throws InterruptedException, IOException {
    LOGGER.info("Check painting photo");
    Thread.sleep(3000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(paintingContent.screenshot()));
    assertFalse(new ScreenDiffResult(
        actual,
        expectedPhoto
    ));
    return this;
  }

  @Step("Check painting title {0}")
  @Nonnull
  public DetailedPaintingPage checkPaintingTitle(String title) {
    LOGGER.info("Check painting title {}", title);
    paintingTitle.shouldHave(exactText(title));
    return this;
  }

  @Step("Check painting artist {0}")
  @Nonnull
  public DetailedPaintingPage checkPaintingArtist(String artistName) {
    LOGGER.info("Check painting artist {}", artistName);
    artistPainting.shouldHave(exactText(artistName));
    return this;
  }

  @Step("Check painting description {0}")
  @Nonnull
  public DetailedPaintingPage checkPaintingDescription(String description) {
    LOGGER.info("Check painting description {}", description);
    paintingDescription.shouldHave(exactText(description));
    return this;
  }

  @Step("Check that painting updated")
  @Nonnull
  public DetailedPaintingPage checkThatPaintingUpdated(String title, String artist,
      String description, BufferedImage image)
      throws InterruptedException, IOException {
    LOGGER.info("Check that painting updated");
    checkPaintingTitle(title);
    checkPaintingDescription(description);
    checkPaintingArtist(artist);
    checkPaintingPhoto(image);
    return this;
  }
}
