package guru.qa.rococo.data.repository.impl;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import guru.qa.rococo.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = em(CFG.authJdbcUrl());

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  @Nonnull
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    entityManager.joinTransaction();
    return entityManager.merge(user);
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(AuthUserEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try {
      return Optional.of(
          entityManager.createQuery("select u from UserEntity u where u.username =: username", AuthUserEntity.class)
              .setParameter("username", username)
              .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(AuthUserEntity user) {
    entityManager.joinTransaction();
    entityManager.remove(user);
  }
}
