package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

public record UserJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("lastname")
    String lastname,
    @JsonProperty("avatar")
    byte[] avatar
) {

  public @Nonnull UserJson addUsername(@Nonnull String username) {
    return new UserJson(id, username, firstname, lastname, avatar);
  }
}
