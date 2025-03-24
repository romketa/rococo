package guru.qa.rococo.driverFactory;

import com.codeborne.selenide.Configuration;
import guru.qa.rococo.driverFactory.browser.ChromeBrowserImpl;
import guru.qa.rococo.driverFactory.browser.FirefoxBrowserImpl;
import guru.qa.rococo.driverFactory.browser.IBrowser;
import guru.qa.rococo.exception.BrowserTypeNotSupportedException;
import guru.qa.rococo.label.Env;
import guru.qa.rococo.model.BrowserData;

public class DriverFactoryImpl implements IDriverFactory {

  @Override
  public void setUpDriver() {
    BrowserData browser = CFG.browser();
    LOGGER.info("Setting up {} driver", browser.getName());
    IBrowser browserImpl;
    switch (browser) {
      case CHROME -> browserImpl = new ChromeBrowserImpl();
      case FIREFOX -> browserImpl = new FirefoxBrowserImpl();
      default -> {
        try {
          throw new BrowserTypeNotSupportedException(browser.getName());
        } catch (BrowserTypeNotSupportedException driverTypeNotSupported) {
          LOGGER.info("Run default browser chrome");
          browserImpl = new ChromeBrowserImpl();
        }
      }
    }
    setUpBrowser(browserImpl);
  }

  private void setUpBrowser(IBrowser browser) {

    Configuration.headless = false;
    Configuration.timeout = 8000;
    if (Env.DOCKER.equals(CFG.env())) {
      LOGGER.info("Configure remote connection");
      Configuration.remote = String.valueOf(getRemoteUrl());
      Configuration.browser = browser.getType().getName();
      Configuration.browserVersion = browser.getVersion();
    } else {
      LOGGER.info("Configure local connection");
      Configuration.browser = browser.getType().getName();
    }
    Configuration.browserCapabilities = browser.getCapabilities();
  }
}
