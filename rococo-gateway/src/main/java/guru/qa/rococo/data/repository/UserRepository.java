package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Nonnull
  Optional<UserEntity> findByUsername(@Nonnull String username);

}
