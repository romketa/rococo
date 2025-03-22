package guru.qa.rococo.service;


import static io.grpc.Status.NOT_FOUND;

import guru.qa.grpc.rococo.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.UpdateUserRequest;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.grpc.rococo.UserResponse;
import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.model.UserJson;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
public class GrpcUserdataService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataService.class);

  private final UserRepository userRepository;

  @Autowired
  public GrpcUserdataService(UserRepository userRepository) {
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

  @Override
  public void getUser(@Nonnull UserRequest request, StreamObserver<UserResponse> responseObserver) {
    String username = request.getUsername();
    userRepository.findByUsername(username)
        .ifPresentOrElse(userEntity -> {
              UserResponse response = UserEntity.toGrpcMessage(userEntity);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            },
            () -> responseObserver.onError(
                NOT_FOUND.withDescription("Username not found by username: " + username)
                    .asRuntimeException()));
  }

  @Override
  public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
    String username = request.getUsername();
    UserEntity userEntity = userRepository.findByUsername(username)
        .orElseGet(() -> {
          UserEntity emptyUser = new UserEntity();
          emptyUser.setUsername(username);
          return emptyUser;
        });

    userEntity.setFirstname(request.getFirstname());
    userEntity.setLastname(request.getLastname());
    userEntity.setAvatar(request.getAvatar().toByteArray());
    userRepository.save(userEntity);

    UserResponse userResponse = UserEntity.toGrpcMessage(userEntity);
    responseObserver.onNext(userResponse);
    responseObserver.onCompleted();
  }
}
