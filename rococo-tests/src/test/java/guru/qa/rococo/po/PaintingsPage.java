package guru.qa.rococo.po;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.po.component.modal.Painting;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class PaintingsPage extends BasePage<PaintingsPage>{

  private static final Logger LOGGER = LoggerFactory.getLogger(PaintingsPage.class);

  @Step("Check that paintings page loaded")
  @Nonnull
  @Override
  public PaintingsPage checkThatPageLoaded() {
    LOGGER.info("Check that paintings page loaded");
    title.shouldBe(visible, text("Картины"));
    return this;
  }

  @Step("Open paintings page")
  @Nonnull
  public PaintingsPage open() {
    LOGGER.info("Open paintings page");
    Selenide.open(CFG.paintingUrl());
    return this;
  }

  @Step("Check that 'Add Painting' btn hidden")
  @Nonnull
  public PaintingsPage addPaintingButtonShouldBeHidden() {
    LOGGER.info("Check that 'Add painting' btn hidden");
    addBtn.shouldBe(not(visible));
    return this;
  }

  @Step("Filter paintings by title {0}")
  @Nonnull
  public PaintingsPage filterPaintingsByTitle(String title) {
    LOGGER.info("Filter paintings by title {}", title);
    searchInput.setValue(title);
    searchBtn.click();
    return this;
  }

  @Step("Add Painting")
  @Nonnull
  public Painting addPainting() {
    LOGGER.info("Add Painting");
    addBtn.click();
    return new Painting();
  }

  @Step("Check created Painting with title {0}")
  @Nonnull
  public PaintingsPage checkThatPaintingCreated(String title) {
    LOGGER.info("Check created Painting");
    getPaintingFromTheList(title).shouldHave(text(title));
    return this;
  }

  @Step("Open detailed Painting info")
  @Nonnull
  public DetailedPaintingPage openDetailedPaintingInfo(String title) {
    LOGGER.info("Open detailed Painting info");
    getPaintingFromTheList(title).click();
    return new DetailedPaintingPage();
  }

  @Nonnull
  private SelenideElement getPaintingFromTheList(String PaintingTitle) {
    return listOfPageItems.filter(text(PaintingTitle)).first();
  }
}
