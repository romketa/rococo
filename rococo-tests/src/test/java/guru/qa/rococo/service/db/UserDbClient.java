package guru.qa.rococo.service.db;

import static java.util.Objects.requireNonNull;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.UserEntity;
import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import guru.qa.rococo.data.entity.auth.Authority;
import guru.qa.rococo.data.entity.auth.AuthorityEntity;
import guru.qa.rococo.data.repository.AuthUserRepository;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.UserdataRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.UserJson;
import io.qameta.allure.Step;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ParametersAreNonnullByDefault
public class UserDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();
  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.userdataJdbcUrl(), CFG.authJdbcUrl()
  );

  @Nonnull
  @Step("Create user with username {username} via DB")
  public UserJson createUser(String username, String password) {
    UserJson user = Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> UserJson.fromEntity(createNewUser(username, password))));
    return user.setPassword(password);
  }

  @Nonnull
  @Step("Find user by username {username} via DB")
  public UserJson findUserByUsername(String username, String password) {
    UserJson currentUser = UserJson.fromEntity(userdataRepository.findByName(username).orElseThrow());
    return currentUser.setPassword(password);
  }

  @Nonnull
  @Step("Update user via DB")
  public UserJson updateUserInfo(UserJson user) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> UserJson.fromEntity(
            userdataRepository.update(UserEntity.fromJson(user))
        )
    ));
  }

  private UserEntity createNewUser(String username, String password) {
    AuthUserEntity authUser = authUserEntity(username, password);
    authUserRepository.create(authUser);
    return userdataRepository.create(userEntity(username));
  }

  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    return ue;
  }

  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(Arrays.stream(Authority.values()).map(e -> {
      AuthorityEntity ae = new AuthorityEntity();
      ae.setUser(authUser);
      ae.setAuthority(e);
      return ae;
    }).toList());
    return authUser;
  }
}
