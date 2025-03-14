package guru.qa.rococo.po;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import guru.qa.rococo.config.Config;

public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();
  protected final SelenideElement alert;
  private final ElementsCollection formErrors;
  protected final SelenideElement page = $("#page-content");
  protected final SelenideElement title = page.$("h2");
  protected final SelenideElement addBtn = page.$(".variant-filled-primary");
  protected final ElementsCollection listOfPageItems = page.$$("li");
  protected final SelenideElement searchInput = page.$(".input");
  protected final SelenideElement searchBtn = page.$(".btn-icon");

  protected BasePage(SelenideDriver driver) {
    this.alert = driver.$("div[data-testid='toast']");
    this.formErrors = driver.$$("p.Mui-error, .input__helper-text");
  }

  public BasePage() {
    this.alert = Selenide.$("div[data-testid='toast']");
    this.formErrors = Selenide.$$("p.Mui-error, .input__helper-text");
  }

  public abstract T checkThatPageLoaded();

  @Step("Check alert message {0}")
  @Nonnull
  @SuppressWarnings("unchecked")
  public T checkAlert(String message) {
    alert.shouldHave(text(message));
    return (T) this;
  }
}
