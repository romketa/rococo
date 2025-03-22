package guru.qa.rococo.data.repository.impl;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.UserEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UserdataRepositoryHibernate implements UserdataRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

  @Nonnull
  @Override
  public Optional<UserEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(UserEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findByName(String username) {
    return Optional.ofNullable(
        entityManager.find(UserEntity.class, username)
    );
  }

  @Nonnull
  @Override
  public UserEntity create(UserEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  @Nonnull
  @Override
  public UserEntity update(UserEntity user) {
    entityManager.joinTransaction();
    return entityManager.merge(user);
  }
}
