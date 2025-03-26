package guru.qa.rococo.driverFactory.browser;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.BrowserData;
import java.util.HashMap;
import java.util.logging.Level;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import static guru.qa.rococo.model.BrowserData.CHROME;

public class ChromeBrowserImpl implements IBrowser {

  private static final Config CFG = Config.getInstance();

  @Override
  public BrowserData getType() {
    return CHROME;
  }

  @Override
  public String getVersion() {
    return CFG.chromeVersion();
  }

  @Override
  public DesiredCapabilities getCapabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setCapability(
        "selenoid:options",
        new HashMap<String, Object>() {
          {
            put("enableVideo", false);
            put("enableVNC", true);
            put("timeZone", "Europe/Moscow");
          }
        });
    LoggingPreferences logs = new LoggingPreferences();
    logs.enable(LogType.BROWSER, Level.ALL);
    logs.enable(LogType.PERFORMANCE, Level.ALL);
    chromeOptions.setAcceptInsecureCerts(true);
    capabilities.setCapability("goog:loggingPrefs", logs);
    chromeOptions.addArguments("enable-automation");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--disable-extensions");
    chromeOptions.addArguments("--dns-prefetch-disable");
    chromeOptions.addArguments("--disable-gpu");
    capabilities.merge(chromeOptions);
    return capabilities;
  }
}
