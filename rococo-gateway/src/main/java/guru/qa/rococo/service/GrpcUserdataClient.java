package guru.qa.rococo.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.grpc.rococo.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.UpdateUserRequest;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.grpc.rococo.UserResponse;
import guru.qa.rococo.model.UserJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GrpcUserdataClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);

  private final RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub userdataStub;

  @Autowired
  public GrpcUserdataClient(RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub userdataStub) {
    this.userdataStub = userdataStub;
  }

  public @Nonnull UserJson getUser(String username) {
    UserRequest request = UserRequest.newBuilder()
        .setUsername(username)
        .build();
    try {
      UserResponse response = userdataStub.getUser(request);
      return UserJson.fromGrpcMessage(response);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
        throw new ResponseStatusException(NOT_FOUND, "User with username " + username + " not found", e);
      } else {
        LOG.error("### Error while calling gRPC server ", e);
        throw new ResponseStatusException(SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
      }
    }
  }

  public @Nonnull UserJson updateUser(UserJson user) {
    UpdateUserRequest request = UpdateUserRequest.newBuilder()
        .setId(ByteString.copyFromUtf8(user.id().toString()))
        .setUsername(user.username())
        .setFirstname(user.firstname())
        .setLastname(user.lastname())
        .setAvatar(ByteString.copyFrom(user.avatar(), StandardCharsets.UTF_8))
        .build();
    UserResponse response = userdataStub.updateUser(request);
    return UserJson.fromGrpcMessage(response);
  }

}
