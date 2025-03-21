package guru.qa.rococo.service.api;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.qameta.allure.Step;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.UserdataApi;
import guru.qa.rococo.api.core.RestClient.EmptyClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.UserJson;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public class UserApiClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserApiClient.class);
  private static final Config CFG = Config.getInstance();

  private final AuthApi authApiClient = new EmptyClient(CFG.authUrl()).create(AuthApi.class);
  private final UserdataApi userdataApi = new EmptyClient(CFG.userdataUrl()).create(
      UserdataApi.class);

  @Nonnull
  @Step("Create user {username} via API")
  public UserJson createUser(String username, String password) {
    LOGGER.info("Create user {} via API", username);
    try {
      authApiClient.requestRegisterForm().execute();
      authApiClient.register(
          username,
          password,
          password,
          ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
      ).execute();
      UserJson createdUser = requireNonNull(userdataApi.currentUser(username).execute().body());
      return createdUser.setPassword(password);
    } catch (IOException e) {
      throw new RuntimeException("Error occurred while create user via API");
    }
  }

  @Nonnull
  @Step("Find user by username {username} via API")
  public UserJson findUserByUsername(String username, String password) {
    LOGGER.info("Find user by username {} via API", username);
    try {
      UserJson currentUser = requireNonNull(userdataApi.currentUser(username).execute().body());
      return currentUser.setPassword(password);
    } catch (IOException e) {
      throw new RuntimeException("Error occurred while finding user via API");
    }
  }

  @Nonnull
  @Step("Find all users via API")
  public List<UserJson> findAllUsers() {
    LOGGER.info("Get all users via API");
    try {
      Response<List<UserJson>> responseAllUsers = userdataApi.allUsers().execute();
      assertEquals(200, responseAllUsers.code());
      return requireNonNull(responseAllUsers.body());
    } catch (IOException e) {
      throw new RuntimeException("Error occurred while finding all users via API");
    }
  }

  @Nonnull
  @Step("Update user via API")
  public UserJson updateUserInfo(UserJson user) {
    LOGGER.info("Update user {} via API", user.username());
    try {
      Response<UserJson> updatedUserResponse = userdataApi.updateUserInfo(user).execute();
      assertEquals(200, updatedUserResponse.code());
      return requireNonNull(updatedUserResponse.body());
    } catch (IOException e) {
      throw new RuntimeException("Error occurred while create user via API");
    }
  }
}
