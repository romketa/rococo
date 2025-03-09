package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.ArtistEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

  @Nonnull
  Optional<ArtistEntity> findById(@Nonnull UUID id);

  @Nonnull
  Page<ArtistEntity> findAll(@Nonnull Pageable pageable);
}
