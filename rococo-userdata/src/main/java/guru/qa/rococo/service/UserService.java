package guru.qa.rococo.service;

import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}",
                cr.toString()),
            () -> {
              LOG.info("### Kafka consumer record: {}", cr.toString());

              UserEntity userDataEntity = new UserEntity();
              userDataEntity.setUsername(user.username());
              UserEntity userEntity = userRepository.save(userDataEntity);

              LOG.info(
                  "### User '{}' successfully saved to database with id: {}",
                  user.username(),
                  userEntity.getId()
              );
            }
        );
  }

  @Transactional
  public @Nonnull
  UserJson update(@Nonnull UserJson user) {
    UserEntity userEntity = userRepository.findByUsername(user.username())
        .orElseGet(() -> {
          UserEntity emptyUser = new UserEntity();
          emptyUser.setUsername(user.username());
          return emptyUser;
        });

    userEntity.setFirstname(user.firstname());
    userEntity.setLastname(user.lastname());
    if (isAvaString(user.avatar())) {
      userEntity.setAvatar(user.avatar().getBytes(StandardCharsets.UTF_8));
    }
    UserEntity saved = userRepository.save(userEntity);
    return UserJson.fromEntity(saved);
  }

  @Transactional(readOnly = true)
  public @Nonnull
  UserJson getCurrentUser(@Nonnull String username) {
    return userRepository.findByUsername(username).map(UserJson::fromEntity)
        .orElseGet(() -> new UserJson(
            null,
            username,
            null,
            null,
            null
        ));
  }

  @Transactional(readOnly = true)
  public @Nonnull
  List<UserJson> allUsers() {
    List<UserEntity> usersFromDb = userRepository.findAll();

    return usersFromDb.stream()
        .map(UserJson::fromEntity)
        .toList();
  }

  @Transactional(readOnly = true)
  public @Nonnull
  Page<UserJson> allUsers(@Nonnull Pageable pageable) {
    Page<UserEntity> usersFromDb = userRepository.findAll(pageable);

    return usersFromDb.map(UserJson::fromEntity);
  }

  public static boolean isAvaString(String photo) {
    return photo != null && photo.startsWith("data:image");
  }

  @Nonnull
  UserEntity getRequiredUser(@Nonnull String username) {
    return userRepository.findByUsername(username).orElseThrow(
        () -> new NotFoundException("Can`t find user by username: '" + username + "'")
    );
  }
}
