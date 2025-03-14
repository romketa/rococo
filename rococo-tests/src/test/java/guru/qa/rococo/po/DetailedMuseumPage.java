package guru.qa.rococo.po;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.po.component.modal.Museum;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScreenDiffResult;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DetailedMuseumPage extends BasePage<DetailedMuseumPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DetailedMuseumPage.class);
  private final SelenideElement page = $("#page-content");
  private final SelenideElement museumTitle = page.$(".card-header");
  private final SelenideElement museumDescription = page.$(".mx-auto ~ div");
  private final SelenideElement museumGeo = page.$$(".text-center").get(1);
  private final SelenideElement editMuseumBtn = page.$("button[data-testid='edit-museum']");
  private final SelenideElement museumPhoto = page.$("img");

  @Step("Check that detailed Museum page loaded")
  @Nonnull
  @Override
  public DetailedMuseumPage checkThatPageLoaded() {
    LOGGER.info("Check that detailed Museum page loaded");
    museumTitle.shouldBe(visible);
    return this;
  }

  @Step("Edit Museum")
  @Nonnull
  public Museum editMuseum() {
    LOGGER.info("Edit Museum");
    editMuseumBtn.click();
    return new Museum();
  }

  @Step("Check Museum photo")
  @Nonnull
  public DetailedMuseumPage checkMuseumPhoto(BufferedImage expectedPhoto)
      throws InterruptedException, IOException {
    LOGGER.info("Check Museum photo");
    Thread.sleep(3000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(museumPhoto.screenshot()));
    assertFalse(new ScreenDiffResult(
        actual,
        expectedPhoto
    ));
    return this;
  }

  @Step("Check Museum title {0}")
  @Nonnull
  public DetailedMuseumPage checkMuseumTitle(String title) {
    LOGGER.info("Check Museum title {}", title);
    museumTitle.shouldHave(exactText(title));
    return this;
  }

  @Step("Check Museum description {0}")
  @Nonnull
  public DetailedMuseumPage checkMuseumDescription(String description) {
    LOGGER.info("Check Museum description {}", description);
    museumDescription.shouldHave(exactText(description));
    return this;
  }

  @Step("Check that museum updated")
  @Nonnull
  public DetailedMuseumPage checkThatMuseumUpdated(String name, GeoJson geo,
      String description, BufferedImage image)
      throws InterruptedException, IOException {
    LOGGER.info("Check that painting added for the Museum");
    museumTitle.shouldBe(text(name));
    museumGeo.shouldBe(text(geo.country().name() + ", " + geo.city()));
    museumDescription.shouldBe(text(description));
    Thread.sleep(3000);
    BufferedImage actual = ImageIO.read(
        Objects.requireNonNull($("img").scrollIntoView(true).screenshot()));
    assertFalse(new ScreenDiffResult(
        actual,
        image
    ));
    return this;
  }
}
