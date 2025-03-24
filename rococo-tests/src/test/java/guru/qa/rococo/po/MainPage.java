package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import guru.qa.rococo.po.component.Header;

public class MainPage extends BasePage<MainPage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainPage.class);
  public static final String URL = CFG.frontUrl();
  private final SelenideElement mainBlock = $("#page-content");
  private final ElementsCollection blocks = mainBlock.$$("li");
  protected final Header header = new Header();
  private static final String TEXT_ON_MAIN_PAGE = "Ваши любимые картины и художники всегда рядом";


  @Nonnull
  public Header getHeader() {
    return header;
  }

  @Step("Open main page")
  @Nonnull
  public MainPage open() {
    LOGGER.info("Open main page");
    Selenide.open(URL);
    return this;
  }

  @Override
  @Nonnull
  @Step("Check that main page loaded")
  public MainPage checkThatPageLoaded() {
    LOGGER.info("Check that main page loaded");
    mainBlock.find("p").shouldBe(text(TEXT_ON_MAIN_PAGE));
    return this;
  }

  @Step("Go to artists page")
  @Nonnull
  public MainPage goToArtistsPage() {
    LOGGER.info("Go to artists page");
    blocks.get(1).click();
    return this;
  }

  @Step("Go to museums page")
  @Nonnull
  public MainPage goToMuseumsPage() {
    LOGGER.info("Go to museums page");
    blocks.first().click();
    return this;
  }

  @Step("Go to paintings page")
  @Nonnull
  public MainPage goToPaintingsPage() {
    LOGGER.info("Go to paintings page");
    blocks.get(2).click();
    return this;
  }
}
