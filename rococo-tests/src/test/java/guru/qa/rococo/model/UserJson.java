package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import guru.qa.rococo.data.entity.UserEntity;

public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("lastname")
    String lastname,
    @JsonProperty("avatar")
    String avatar,
    @JsonIgnore
    String password
) {

  public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getLastname(),
        entity.getAvatar() != null && entity.getAvatar().length > 0 ? new String(entity.getAvatar(),
            StandardCharsets.UTF_8) : null,
        null
    );
  }

  public UserJson setPassword(String password) {
    return new UserJson(id, username, firstname, lastname, avatar, password);
  }

  public UserJson(@Nonnull String username, @Nullable String password) {
    this(null, username, null, null, null, password);
  }
}
