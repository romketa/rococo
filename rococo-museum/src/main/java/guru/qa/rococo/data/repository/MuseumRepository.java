package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {

  @Nonnull
  Optional<MuseumEntity> findByIdAndTitle(@Nonnull UUID id, @Nonnull String title);

  @Nonnull
  Page<MuseumEntity> findByTitleContainingIgnoreCase(@Nonnull String title, @Nonnull Pageable pageable);

  @Nonnull
  Optional<MuseumEntity> findByTitle(@Nonnull String title);
}
