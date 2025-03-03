package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.CountryEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

  @Nonnull
  Optional<CountryEntity> findByIdAndName(@Nonnull UUID id, @Nonnull String name);

  Page<CountryEntity> findAll(@Nonnull Pageable pageable);
}
