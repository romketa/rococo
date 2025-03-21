package guru.qa.rococo.config;


import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

enum LocalConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @NotNull
  @Override
  public String authorizedUrl() {
    return "http://127.0.0.1:3000/authorized";
  }

  @Nonnull
  @Override
  public String ghUrl() {
    return "https://api.github.com/";
  }

  @Nonnull
  @Override
  public String registerUrl() {
    return "http://127.0.0.1:9000/register";
  }

  @Nonnull
  @Override
  public String paintingUrl() {
    return "http://127.0.0.1:3000/painting";
  }

  @Nonnull
  @Override
  public String paintingJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-painting";
  }

  @NotNull
  @Override
  public String artistUrl() {
    return "http://127.0.0.1:3000/artist";
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-artist";
  }

  @NotNull
  @Override
  public String museumUrl() {
    return "http://127.0.0.1:3000/museum";
  }

  @NotNull
  @Override
  public String museumJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-museum";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-auth";
  }

  @Nonnull
  @Override
  public String countryJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-country";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8080/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://127.0.0.1:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-userdata";
  }

  @NotNull
  @Override
  public String grpcAddress() {
    return "127.0.0.1";
  }
}
