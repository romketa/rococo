package guru.qa.rococo.driverFactory;

import guru.qa.rococo.config.Config;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IDriverFactory {

  Config CFG = Config.getInstance();
  Logger LOGGER = LoggerFactory.getLogger(IDriverFactory.class);

  void setUpDriver();

  default URL getRemoteUrl() {
    try {
      return new URI(CFG.remoteUrl()).toURL();
    } catch (MalformedURLException e) {
      LOGGER.error("Malformed URL for Web");
      return null;
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }


}
