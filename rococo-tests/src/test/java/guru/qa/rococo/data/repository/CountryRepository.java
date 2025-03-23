package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.CountryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public interface CountryRepository {

  @Nonnull
  Optional<CountryEntity> findById(UUID id);

  @Nonnull
  Optional<CountryEntity> findByName(String name);

  @Nonnull
  List<CountryEntity> getAllCountries();
}
