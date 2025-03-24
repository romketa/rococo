package guru.qa.rococo.test.grpc;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.RandomDataUtils.randomLastName;
import static utils.RandomDataUtils.randomName;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.UpdateUserRequest;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.grpc.rococo.UserResponse;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.label.AllureEpic;
import guru.qa.rococo.label.AllureFeature;
import guru.qa.rococo.label.JTag;
import guru.qa.rococo.model.UserJson;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.GrpcConsoleInterceptor;
import utils.ImageUtils;


@Owner("Roman Nagovitcyn")
@Severity(SeverityLevel.NORMAL)
@Epic(AllureEpic.GRPC)
@Feature(AllureFeature.USERDATA)
@Tag(JTag.GRPC)
@DisplayName("GRPC: Rococo-userdata service tests")
public class UserdataTest extends BaseTest {

  private static final Channel userdataChannel = ManagedChannelBuilder
      .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  private static final RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub userdataStub
      = RococoUserdataServiceGrpc.newBlockingStub(userdataChannel);


  @Test
  @User
  @DisplayName("GRPC: Verify that created user has empty fields")
  void verifyThatCreatedUserHasEmptyFields(UserJson user) {
    UserRequest request = UserRequest.newBuilder().setUsername(user.username()).build();
    UserResponse response = userdataStub.getUser(request);

    step("Check that created user has correct values", () -> {
      assertEquals(user.username(), response.getUsername());
      assertTrue(response.getFirstname().isEmpty());
      assertTrue(response.getLastname().isEmpty());
      assertTrue(response.getAvatar().isEmpty());
    });
  }

  @Test
  @DisplayName("GRPC: Error should be occurred for non-existing user")
  void errorShouldBeOccurredInCaseNonExistingUser() {
    UserRequest request = UserRequest.newBuilder().setUsername("unknown").build();
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> userdataStub.getUser(request));

    step("Check that painting was not found",
        () -> assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode()));
  }

  @Test
  @User
  @DisplayName("GRPC: Verify that user can be updated")
  void userCanBeUpdated(UserJson user) {
    String firstName = randomName();
    String lastName = randomLastName();
    ByteString ava = copyFromUtf8(ImageUtils.convertImgToBase64("img/ava/spider-man-ava.jpg"));

    UpdateUserRequest request = UpdateUserRequest.newBuilder()
        .setUsername(user.username())
        .setFirstname(firstName)
        .setLastname(lastName)
        .setAvatar(ava)
        .build();

    UserResponse response = userdataStub.updateUser(request);

    step("Check that user updated successfully", () -> {
      assertEquals(user.username(), response.getUsername());
      assertEquals(firstName, response.getFirstname());
      assertEquals(lastName, response.getLastname());
      assertEquals(ava, response.getAvatar());
    });
  }
}
