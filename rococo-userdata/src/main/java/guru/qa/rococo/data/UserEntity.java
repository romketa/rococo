package guru.qa.rococo.data;

import static com.google.protobuf.ByteString.copyFromUtf8;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.UserResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID()))")
  private UUID id;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "firstname")
  private String firstname;

  @Column(name = "lastname")
  private String lastname;

  @Column(name = "avatar", columnDefinition = "LONGBLOB")
  private byte[] avatar;

  public static UserResponse toGrpcMessage(UserEntity entity) {
    UserResponse.Builder builder = UserResponse.newBuilder()
        .setId(copyFromUtf8(entity.getId().toString()))
        .setUsername(entity.getUsername());

    if (entity.getFirstname() != null) {
      builder.setFirstname(entity.getFirstname());
    }
    if (entity.getLastname() != null) {
      builder.setLastname(entity.getLastname());
    }
    if (entity.getAvatar() != null) {
      builder.setAvatar(ByteString.copyFrom(entity.getAvatar()));
    }

    return builder.build();
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    Class<?> oEffectiveClass = object instanceof HibernateProxy
        ? ((HibernateProxy) object).getHibernateLazyInitializer()
        .getPersistentClass() : object.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    UserEntity that = (UserEntity) object;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass().hashCode() : getClass().hashCode();
  }
}
