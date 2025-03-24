package guru.qa.rococo.driverFactory.browser;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.BrowserData;
import java.util.HashMap;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import static guru.qa.rococo.model.BrowserData.FIREFOX;

public class FirefoxBrowserImpl implements IBrowser {

  private final static Config CFG = Config.getInstance();
  @Override
  public BrowserData getType() {
    return FIREFOX;
  }

  @Override
  public String getVersion() {
    return CFG.firefoxVersion();
  }

  @Override
  public DesiredCapabilities getCapabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setCapability("selenoid:options", new HashMap<String, Object>() {{
      put("enableVideo", false);
      put("enableVNC", true);
    }});
    FirefoxProfile firefoxProfile = new FirefoxProfile();
    firefoxOptions.setProfile(firefoxProfile);
    firefoxOptions.setCapability("moz:webdriverClick", false);
    firefoxOptions.setAcceptInsecureCerts(true);
    capabilities.merge(firefoxOptions);
    return capabilities;
  }
}
