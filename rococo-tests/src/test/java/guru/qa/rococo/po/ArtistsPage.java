package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.po.component.modal.Artist;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtistsPage extends BasePage<ArtistsPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArtistsPage.class);

  @Step("Check that page loaded")
  @Nonnull
  @Override
  public ArtistsPage checkThatPageLoaded() {
    LOGGER.info("Check that page loaded");
    title.shouldBe(visible, text("Художники"));
    return this;
  }

  @Step("Open artists page")
  @Nonnull
  public ArtistsPage open() {
    LOGGER.info("Open artists page");
    Selenide.open(CFG.artistUrl());
    return this;
  }

  @Step("Check that 'Add Artist' btn hidden")
  @Nonnull
  public ArtistsPage addArtistButtonShouldBeHidden() {
    LOGGER.info("Check that 'Add Artist' btn hidden");
    addBtn.shouldBe(not(visible));
    return this;
  }

  @Step("Filter artists by name {0}")
  @Nonnull
  public ArtistsPage filterArtistsByName(String name) {
    LOGGER.info("Filter artists by name {}", name);
    searchInput.setValue(name);
    searchBtn.click();
    return this;
  }

  @Step("Add Artist")
  @Nonnull
  public Artist addArtist() {
    LOGGER.info("Add Artist");
    addBtn.click();
    return new Artist();
  }

  @Step("Check created artist with name {0}")
  @Nonnull
  public ArtistsPage checkThatArtistCreated(String name) {
    LOGGER.info("Check created artist");
    getArtistFromTheList(name).shouldHave(text(name));
    return this;
  }

  @Step("Open detailed artist info")
  @Nonnull
  public DetailedArtistPage openDetailedArtistInfo(String name) {
    LOGGER.info("Check created artist");
    getArtistFromTheList(name).click();
    return new DetailedArtistPage();
  }

  @Nonnull
  private SelenideElement getArtistFromTheList(String artistName) {
    return listOfPageItems.filter(text(artistName)).first();
  }

  @Nonnull
  @Step("Check message after no found artists")
  public ArtistsPage checkMessageNoArtistsFound() {
    infoMessage.first().shouldBe(text("Художники не найдены"));
    infoMessage.get(1).shouldBe(text("Для указанного вами фильтра мы не смогли найти художников"));
    return this;
  }
}
