package guru.qa.rococo.config;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @NotNull
  @Override
  public String authorizedUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.niffler.dc:8090/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.niffler.dc:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-userdata";
  }

  @NotNull
  @Override
  public String grpcAddress() {
    return "grpc.rococo.dc";
  }

  @Nonnull
  @Override
  public String registerUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String paintingUrl() {
    return "";
  }

  @NotNull
  @Override
  public String paintingJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String artistUrl() {
    return "";
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String museumUrl() {
    return "";
  }

  @NotNull
  @Override
  public String museumJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String countryJdbcUrl() {
    return "";
  }
}
