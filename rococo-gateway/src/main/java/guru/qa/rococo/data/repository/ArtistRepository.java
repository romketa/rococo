package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.ArtistEntity;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

  @Nonnull
  List<ArtistEntity> findByName();

}
