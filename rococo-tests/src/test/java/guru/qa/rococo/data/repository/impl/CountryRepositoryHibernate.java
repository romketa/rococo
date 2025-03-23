package guru.qa.rococo.data.repository.impl;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class CountryRepositoryHibernate implements CountryRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager entityManager = em(CFG.countryJdbcUrl());

  @Nonnull
  @Override
  public Optional<CountryEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(CountryEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public Optional<CountryEntity> findByName(String name) {
    return Optional.ofNullable(
        entityManager.find(CountryEntity.class, name)
    );
  }

  @Nonnull
  @Override
  public List<CountryEntity> getAllCountries() {
    return entityManager.createQuery("select c from CountryEntity c", CountryEntity.class)
        .getResultList();
  }
}
