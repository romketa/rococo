package guru.qa.rococo.po;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.po.component.modal.Museum;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MuseumsPage extends BasePage<MuseumsPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MuseumsPage.class);

  @Step("Check that museums page loaded")
  @Nonnull
  @Override
  public MuseumsPage checkThatPageLoaded() {
    LOGGER.info("Check that museums page loaded");
    title.shouldBe(visible, text("Музеи"));
    return this;
  }

  @Step("Open museums page")
  @Nonnull
  public MuseumsPage open() {
    LOGGER.info("Open museums page");
    Selenide.open(CFG.museumUrl());
    return this;
  }

  @Step("Check that 'Add Museum' btn hidden")
  @Nonnull
  public MuseumsPage addMuseumButtonShouldBeHidden() {
    LOGGER.info("Check that 'Add museum' btn hidden");
    addBtn.shouldBe(not(visible));
    return this;
  }

  @Step("Filter museums by title {0}")
  @Nonnull
  public MuseumsPage filterMuseumsByTitle(String title) {
    LOGGER.info("Filter museums by title {}", title);
    searchInput.setValue(title);
    searchBtn.click();
    return this;
  }

  @Step("Add Museum")
  @Nonnull
  public Museum addMuseum() {
    LOGGER.info("Add Museum");
    addBtn.click();
    return new Museum();
  }

  @Step("Check created Museum with title {0}")
  @Nonnull
  public MuseumsPage checkThatMuseumCreated(String title) {
    LOGGER.info("Check created museum");
    getMuseumFromTheList(title).shouldHave(text(title));
    return this;
  }

  @Step("Open detailed Museum info")
  @Nonnull
  public DetailedMuseumPage openDetailedMuseumInfo(String title) {
    LOGGER.info("Open detailed museum info");
    getMuseumFromTheList(title).click();
    return new DetailedMuseumPage();
  }

  @Nonnull
  private SelenideElement getMuseumFromTheList(String MuseumTitle) {
    return listOfPageItems.filter(text(MuseumTitle)).first();
  }
}
