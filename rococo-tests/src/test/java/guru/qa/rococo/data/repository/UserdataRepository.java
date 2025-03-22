package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface UserdataRepository {

  @Nonnull
  Optional<UserEntity> findById(UUID id);

  @Nonnull
  Optional<UserEntity> findByName(String username);

  @Nonnull
  UserEntity create(UserEntity user);

  @Nonnull
  UserEntity update(UserEntity user);
}
