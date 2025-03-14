package guru.qa.rococo.data.repository.impl;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

import guru.qa.rococo.data.repository.ArtistRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.ArtistEntity;

public class ArtistRepositoryHibernate implements ArtistRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager entityManager = em(CFG.artistJdbcUrl());

  @Nonnull
  @Override
  public Optional<ArtistEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(ArtistEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public ArtistEntity create(ArtistEntity artist) {
    entityManager.joinTransaction();
    entityManager.persist(artist);
    return artist;
  }

  @Nonnull
  @Override
  public ArtistEntity update(ArtistEntity artist) {
    entityManager.joinTransaction();
    return entityManager.merge(artist);
  }
}
