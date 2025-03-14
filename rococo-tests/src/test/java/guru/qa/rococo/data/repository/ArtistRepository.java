package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.ArtistEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface ArtistRepository {

  Optional<ArtistEntity> findById(UUID id);

  @Nonnull
  ArtistEntity create(ArtistEntity artist);

  @Nonnull
  ArtistEntity update(ArtistEntity artist);
}
