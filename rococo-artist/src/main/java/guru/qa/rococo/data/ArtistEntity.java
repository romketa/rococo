package guru.qa.rococo.data;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.ArtistResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "artist")
public class ArtistEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID()))")
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "biography", columnDefinition = "text")
  private String biography;

  @Lob
  @Column(name = "photo", columnDefinition = "LONGBLOB")
  private byte[] photo;

  public static ArtistResponse toGrpcMessage(ArtistEntity artistEntity) {
    return ArtistResponse.newBuilder()
        .setId(ByteString.copyFromUtf8(artistEntity.getId().toString()))
        .setBiography(artistEntity.getBiography())
        .setName(artistEntity.getName())
        .setPhoto(ByteString.copyFrom(artistEntity.getPhoto()))
        .build();
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
    ArtistEntity that = (ArtistEntity) object;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass().hashCode() : getClass().hashCode();
  }
}
