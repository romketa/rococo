package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {

  @Nonnull
  AuthUserEntity create(AuthUserEntity user);

  @Nonnull
  AuthUserEntity update(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);

  void remove(AuthUserEntity user);
}
