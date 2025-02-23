package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

  @Nonnull
  @Query(
      "select pe from PaintingEntity pe join ArtistEntity ae on pe.artist = ae " +
          "where pe.id = :id "
  )
  Page<PaintingEntity> findByArtist(@Nonnull Pageable pageable, @Nonnull UUID id);
}
