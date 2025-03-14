package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.MuseumEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface MuseumRepository {

  @Nonnull
  Optional<MuseumEntity> findById(UUID id);

  @Nonnull
  MuseumEntity findByTitle(String title);

  @Nonnull
  MuseumEntity create(MuseumEntity museum);

  @Nonnull
  MuseumEntity update(MuseumEntity museum);
}
