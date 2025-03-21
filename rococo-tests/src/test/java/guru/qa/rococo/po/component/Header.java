package guru.qa.rococo.po.component;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import guru.qa.rococo.data.PageMode;
import guru.qa.rococo.po.ArtistsPage;
import guru.qa.rococo.po.LoginPage;
import guru.qa.rococo.po.MuseumsPage;
import guru.qa.rococo.po.PaintingsPage;
import guru.qa.rococo.po.component.modal.Profile;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Header.class);
  private final SelenideElement toMainPage = self.find("a[href='/main']");
  private final ElementsCollection menuItems = self.$$("li");
  private final SelenideElement loginBtn = self.$(".btn");
  private final SelenideElement avatar = self.$(".btn-icon");
  private final SelenideElement lightThemeSwitcher = self.$("div[aria-label='Light Switch']");

  public Header() {
    super($("#shell-header"));
  }


  public void checkHeaderText() {
    self.$("h1").shouldHave(text("Niffler"));
  }

  @Nonnull
  @Step("Go to login page")
  public LoginPage toLoginPage() {
    LOGGER.info("Go to login page");
    loginBtn.click();
    return new LoginPage();
  }

  @Nonnull
  @Step("Go to paintings page")
  public PaintingsPage toPaintingsPage() {
    LOGGER.info("Go to paintings page");
    menuItems.first().click();
    return new PaintingsPage();
  }

  @Nonnull
  @Step("Go to artists page")
  public ArtistsPage toArtistsPage() {
    LOGGER.info("Go to artists page");
    menuItems.get(1).click();
    return new ArtistsPage();
  }

  @Nonnull
  @Step("Go to museums page")
  public MuseumsPage toMuseumsPage() {
    LOGGER.info("Go to museums page");
    menuItems.get(2).click();
    return new MuseumsPage();
  }

  @Nonnull
  @Step("Open profile popup")
  public Profile openProfile() {
    LOGGER.info("Go to profile page");
    avatar.click();
    return new Profile();
  }

  @Nonnull
  @Step("Switch page to another light mode - {pageMode}")
  public Header switchLightTheme(PageMode pageMode) {
    LOGGER.info("Switch page to another light mode - {}", pageMode.name());
    if (pageMode.equals(PageMode.LIGHT) && lightThemeSwitcher.has(
        attribute("aria-checked", "false"))) {
      lightThemeSwitcher.click();
    } else if ((pageMode.equals(PageMode.DARK) && lightThemeSwitcher.has(
        attribute("aria-checked", "true")))) {
      lightThemeSwitcher.click();
    }
    return this;
  }
}
