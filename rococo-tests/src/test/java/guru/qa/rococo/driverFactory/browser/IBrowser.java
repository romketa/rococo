package guru.qa.rococo.driverFactory.browser;

import guru.qa.rococo.model.BrowserData;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface IBrowser {

  BrowserData getType();

  String getVersion();

  DesiredCapabilities getCapabilities();
}
