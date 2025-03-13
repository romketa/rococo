package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {

  @Nonnull
  Optional<MuseumEntity> findByIdAndTitle(@Nonnull UUID id, @Nonnull String title);

  @Nonnull
  Page<MuseumEntity> findAll(@Nonnull Pageable pageable);

  @Nonnull
  Optional<MuseumEntity> findByTitle(@Nonnull String title);
}
