package guru.qa.rococo.service;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.model.page.RestPage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(prefix = "rococo-userdata", name = "client", havingValue = "rest")
public class UserDataClient {

  private final RestTemplate restTemplate;
  private final String rococoUserdataApiUri;

  @Autowired
  public UserDataClient(RestTemplate restTemplate,
      @Value("${rococo-userdata.base-uri}") String rococoUserdataBaseUri) {
    this.restTemplate = restTemplate;
    this.rococoUserdataApiUri = rococoUserdataBaseUri + "/internal";
  }

  @Nonnull
  public UserJson currentUser(@Nonnull String username) {
    return Optional.ofNullable(
        restTemplate.getForObject(
            rococoUserdataApiUri + "/users/current?username={username}",
            UserJson.class,
            username
        )
    ).orElseThrow(() -> new NoRestResponseException(
        "No REST UserJson response is given [/users/current/ Route]"));
  }

  @Nonnull
  public UserJson updateUserInfo(@Nonnull UserJson user) {
    return Optional.ofNullable(
        restTemplate.postForObject(
            rococoUserdataApiUri + "/users/update",
            user,
            UserJson.class
        )
    ).orElseThrow(() -> new NoRestResponseException(
        "No REST UserJson response is given [/users/update/ Route]"));
  }

  public List<UserJson> allUsers(@Nonnull String username, @Nullable String searchQuery) {
    return Arrays.asList(
        Optional.ofNullable(
            restTemplate.getForObject(
                rococoUserdataApiUri + "/users/all?username={username}&searchQuery={searchQuery}",
                UserJson[].class,
                username,
                searchQuery
            )
        ).orElseThrow(() -> new NoRestResponseException(
            "No REST UserJson[] response is given [/users/all/ Route]"))
    );
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public Page<UserJson> allUsers(@Nonnull String username, @Nonnull Pageable pageable,
      @Nullable String searchQuery) {
    return Optional.ofNullable(
        restTemplate.getForObject(
            rococoUserdataApiUri + "/v2/users/all?username={username}&searchQuery={searchQuery}"
                + new HttpQueryPaginationAndSort(pageable),
            RestPage.class,
            username,
            searchQuery
        )
    ).orElseThrow(() -> new NoRestResponseException(
        "No REST Page<UserJson> response is given [/v2/users/all/ Route]"));
  }
}

