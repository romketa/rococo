package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.PaintingEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface PaintingRepository {

  @Nonnull
  Optional<PaintingEntity> findById(UUID id);

  @Nonnull
  PaintingEntity create(PaintingEntity painting);

  @Nonnull
  PaintingEntity update(PaintingEntity painting);
}
