package guru.qa.rococo.data;

import guru.qa.grpc.rococo.AddArtistRequest;
import guru.qa.grpc.rococo.ArtistResponse;
import guru.qa.grpc.rococo.EditArtistRequest;
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

import static com.google.protobuf.ByteString.copyFrom;
import static com.google.protobuf.ByteString.copyFromUtf8;

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

    public static ArtistEntity fromEditArtistGrpcMessage(EditArtistRequest request) {
        ArtistEntity entity = fromAddArtistGrpcMessage(request.getArtistData());
        entity.setId(UUID.fromString(request.getId().toStringUtf8()));
        return entity;
    }

    public static ArtistEntity fromAddArtistGrpcMessage(AddArtistRequest request) {
        ArtistEntity entity = new ArtistEntity();
        entity.setName(request.getName());
        entity.setBiography(request.getBiography());
        entity.setPhoto(request.getPhoto().toByteArray());
        return entity;
    }

    public static ArtistResponse toGrpcMessage(ArtistEntity artistEntity) {
        return ArtistResponse.newBuilder()
                .setId(copyFromUtf8(artistEntity.getId().toString()))
                .setBiography(artistEntity.getBiography())
                .setName(artistEntity.getName())
                .setPhoto(copyFrom(artistEntity.getPhoto()))
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
