package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

  @Nonnull
  Page<PaintingEntity> findByArtistId(@Nonnull Pageable pageable, @Nonnull UUID id);

  @Nonnull
  Page<PaintingEntity> findAll(@Nonnull Pageable pageable);

  @Nonnull
  Optional<PaintingEntity> findByTitle(@Nonnull String title);
}
