package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.po.component.modal.Artist;
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

public class DetailedArtistPage extends BasePage<DetailedArtistPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DetailedArtistPage.class);
  private final SelenideElement page = $("#page-content");
  private final SelenideElement artistName = page.$(".card-header");
  private final SelenideElement artistBio = page.$(".col-span-2");
  private final ElementsCollection addPaintingButtons = page.$$(".variant-filled-primary");
  private final SelenideElement editArtistBtn = page.$("button[data-testid='edit-artist']");
  private final SelenideElement artistPhoto = page.$(".avatar-image");
  private final ElementsCollection artistPaintings = page.$$("li");

  @Step("Check that detailed Artist page loaded")
  @Nonnull
  @Override
  public DetailedArtistPage checkThatPageLoaded() {
    LOGGER.info("Check that page detailed Artist page loaded");
    artistName.shouldBe(visible);
    return this;
  }

  @Step("Edit Artist")
  @Nonnull
  public Artist editArtist() {
    LOGGER.info("Edit Artist");
    editArtistBtn.click();
    return new Artist();
  }

  @Step("Check artist photo")
  @Nonnull
  public DetailedArtistPage checkArtistPhoto(BufferedImage expectedPhoto)
      throws InterruptedException, IOException {
    LOGGER.info("Check artist photo");
    Thread.sleep(3000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(artistPhoto.screenshot()));
    assertFalse(new ScreenDiffResult(
        actual,
        expectedPhoto
    ));
    return this;
  }

  @Step("Check artist name {0}")
  @Nonnull
  public DetailedArtistPage checkArtistName(String name) {
    LOGGER.info("Check artist name {}", name);
    artistName.shouldHave(exactText(name));
    return this;
  }

  @Step("Check artist bio {0}")
  @Nonnull
  public DetailedArtistPage checkArtistBiography(String bio) {
    LOGGER.info("Check artist bio {}", bio);
    artistBio.shouldHave(exactText(bio));
    return this;
  }

  @Step("Add painting")
  @Nonnull
  public Painting addPainting() {
    LOGGER.info("Add painting");
    editArtistBtn.shouldBe(visible);
    addPaintingButtons.first().click();
    return new Painting();
  }

  @Step("Add first painting")
  @Nonnull
  public Painting addFirstPainting() {
    LOGGER.info("Add first painting");
    addPaintingButtons.get(1).click();
    return new Painting();
  }

  @Step("Check that painting {0} added for the artist")
  @Nonnull
  public Painting checkThatPaintingAddedForArtist(String paintingName, BufferedImage image)
      throws InterruptedException, IOException {
    LOGGER.info("Check that painting added for the artist");
    SelenideElement addedPainting = artistPaintings.filter(text(paintingName)).first();
    addedPainting.scrollIntoView(true).shouldBe(visible);
    Thread.sleep(3000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(addedPainting.$("img").screenshot()));
    assertFalse(new ScreenDiffResult(
        actual,
        image
    ));
    return new Painting();
  }
}
