package guru.qa.rococo.data.repository.impl;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class MuseumRepositoryHibernate implements MuseumRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager entityManager = em(CFG.museumJdbcUrl());

  @Nonnull
  @Override
  public Optional<MuseumEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(MuseumEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public MuseumEntity findByTitle(String title) {
    return entityManager.createQuery("select m from MuseumEntity m where m.title =: title ", MuseumEntity.class)
        .setParameter("title", title)
        .getSingleResult();
  }

  @Nonnull
  @Override
  public MuseumEntity create(MuseumEntity museum) {
    entityManager.joinTransaction();
    entityManager.persist(museum);
    return museum;
  }

  @Nonnull
  @Override
  public MuseumEntity update(MuseumEntity museum) {
    entityManager.joinTransaction();
    return entityManager.merge(museum);
  }
}
