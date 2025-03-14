package guru.qa.rococo.data.repository.impl;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class PaintingRepositoryHibernate implements PaintingRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager entityManager = em(CFG.paintingJdbcUrl());

  @Nonnull
  @Override
  public Optional<PaintingEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(PaintingEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public PaintingEntity create(PaintingEntity painting) {
    entityManager.joinTransaction();
    entityManager.persist(painting);
    return painting;
  }

  @Nonnull
  @Override
  public PaintingEntity update(PaintingEntity painting) {
    entityManager.joinTransaction();
    return entityManager.merge(painting);
  }
}
