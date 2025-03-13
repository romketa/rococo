package guru.qa.rococo.data;

import guru.qa.grpc.rococo.CountryResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

import static com.google.protobuf.ByteString.copyFromUtf8;

@Getter
@Setter
@Entity
@Table(name = "country")
public class CountryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID()))")
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  public static CountryResponse toGrpcMessage(CountryEntity countryEntity) {
    return CountryResponse.newBuilder()
            .setId(copyFromUtf8(countryEntity.getId().toString()))
            .setName(countryEntity.getName())
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
    CountryEntity that = (CountryEntity) object;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass().hashCode() : getClass().hashCode();
  }
}
