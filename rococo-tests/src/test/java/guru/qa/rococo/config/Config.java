package guru.qa.rococo.config;

import static guru.qa.rococo.model.BrowserData.CHROME;

import guru.qa.rococo.label.Env;
import guru.qa.rococo.model.BrowserData;
import javax.annotation.Nonnull;

public interface Config {

  static @Nonnull Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
  }

  @Nonnull
  Env env();

  @Nonnull
  default BrowserData browser() {
    return BrowserData.valueOf(System.getProperty("browser", "CHROME"));
  }

  @Nonnull
  default String remoteUrl() {
    return "http://selenoid:4444/wd/hub";
  }

  default String chromeVersion() {
    return "127.0";
  }

  default String firefoxVersion() {
    return "125.0";
  }

  @Nonnull
  default String projectId() {
    return "rococo";
  }

  @Nonnull
  String frontUrl();

  @Nonnull
  String authorizedUrl();

  @Nonnull
  String registerUrl();

  @Nonnull
  String paintingUrl();

  @Nonnull
  String paintingJdbcUrl();

  @Nonnull
  String artistUrl();

  @Nonnull
  String artistJdbcUrl();

  @Nonnull
  String museumUrl();

  @Nonnull
  String museumJdbcUrl();

  @Nonnull
  String countryJdbcUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String authJdbcUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  String userdataUrl();

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String userdataGrpcAddress();

  @Nonnull
  String artistGrpcAddress();

  @Nonnull
  String countryGrpcAddress();

  @Nonnull
  String museumGrpcAddress();

  @Nonnull
  String paintingGrpcAddress();

  default int userdataGrpcPort() {
    return 8089;
  }

  default int artistGrpcPort() {
    return 8091;
  }

  default int countryGrpcPort() {
    return 8092;
  }

  default int museumGrpcPort() {
    return 8093;
  }

  default int paintingGrpcPort() {
    return 8094;
  }

  @Nonnull
  default String ghUrl() {
    return "https://api.github.com/";
  }

  String allureDockerServiceUrl();
}
