package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.UserResponse;
import jakarta.annotation.Nonnull;
import java.util.UUID;

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
    String avatar
) {

  public @Nonnull UserJson addUsername(@Nonnull String username) {
    return new UserJson(id, username, firstname, lastname, avatar);
  }

  public static UserJson fromGrpcMessage(UserResponse response) {
    return new UserJson(
        UUID.fromString(response.getId().toStringUtf8()),
        response.getUsername(),
        response.getFirstname(),
        response.getLastname(),
        response.getAvatar().toStringUtf8()
    );
  }
}
