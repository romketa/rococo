package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

  static @Nonnull Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
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
  String grpcAddress();

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
}
