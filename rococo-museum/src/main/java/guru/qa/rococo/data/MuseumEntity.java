package guru.qa.rococo.data;

import guru.qa.grpc.rococo.*;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

import static com.google.protobuf.ByteString.copyFrom;
import static com.google.protobuf.ByteString.copyFromUtf8;

@Getter
@Setter
@Entity
@Table(name = "museum")
public class MuseumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID()))")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(nullable = false)
    private String city;

    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] photo;

    @Column(name = "country_id")
    private UUID countryId;

    @Nonnull
    public static MuseumEntity fromEditMuseumGrpcMessage(@Nonnull EditMuseumRequest request) {
        MuseumEntity entity = fromAddMuseumGrpcMessage(request.getMuseumData());
        entity.setId(UUID.fromString(request.getId().toStringUtf8()));
        return entity;
    }

    @Nonnull
    public static MuseumEntity fromAddMuseumGrpcMessage(@Nonnull AddMuseumRequest request) {
        MuseumEntity entity = new MuseumEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCity(request.getGeo().getCity());
        entity.setPhoto(request.getPhoto().toByteArray());
        entity.setCountryId(UUID.fromString(request.getGeo().getCountry().getId().toStringUtf8()));
        return entity;
    }

    @Nonnull
    public static MuseumResponse toGrpcMessage(@Nonnull MuseumEntity museumEntity) {
        CountryId countryId = CountryId.newBuilder()
                .setId(copyFromUtf8(museumEntity.getCountryId().toString()))
                .build();
        Geo geo = Geo.newBuilder()
                .setCity(museumEntity.getCity())
                .setCountry(countryId)
                .build();
        return MuseumResponse.newBuilder()
                .setId(copyFromUtf8(museumEntity.getId().toString()))
                .setTitle(museumEntity.getTitle())
                .setDescription(museumEntity.getDescription())
                .setPhoto(copyFrom(museumEntity.getPhoto()))
                .setGeo(geo)
                .build();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        MuseumEntity that = (MuseumEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass().hashCode() : getClass().hashCode();
    }
}
