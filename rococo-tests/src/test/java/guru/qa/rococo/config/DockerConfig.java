package guru.qa.rococo.config;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.rococo.dc/";
  }

  @NotNull
  @Override
  public String authorizedUrl() {
    return "http://frontend.rococo.dc/authorized";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.rococo.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rococo.dc:8080/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.rococo.dc:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-userdata";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "userdata.rococo.dc";
  }

  @NotNull
  @Override
  public String artistGrpcAddress() {
    return "artist.rococo.dc";
  }

  @NotNull
  @Override
  public String countryGrpcAddress() {
    return "country.rococo.dc";
  }

  @NotNull
  @Override
  public String museumGrpcAddress() {
    return "museum.rococo.dc";
  }

  @NotNull
  @Override
  public String paintingGrpcAddress() {
    return "painting.rococo.dc";
  }

  @Nonnull
  @Override
  public String registerUrl() {
    return "http://auth.rococo.dc:9000/register";
  }

  @Nonnull
  @Override
  public String paintingUrl() {
    return "http://frontend.rococo.dc/painting";
  }

  @NotNull
  @Override
  public String paintingJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-painting";
  }

  @NotNull
  @Override
  public String artistUrl() {
    return "http://frontend.rococo.dc/artist";
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-artist";
  }

  @NotNull
  @Override
  public String museumUrl() {
    return "http://frontend.rococo.dc/museum";
  }

  @NotNull
  @Override
  public String museumJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-museum";
  }

  @NotNull
  @Override
  public String countryJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-country";
  }
}
