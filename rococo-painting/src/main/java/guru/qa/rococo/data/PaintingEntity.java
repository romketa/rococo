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
@Table(name = "painting")
public class PaintingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID()))")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] content;

    @Column(name = "museum_id")
    private UUID museumId;

    @Column(name = "artist_id")
    private UUID artistId;

    @Nonnull
    public static PaintingEntity fromEditPaintingGrpcMessage(@Nonnull EditPaintingRequest request) {
        PaintingEntity entity = fromAddMuseumGrpcMessage(request.getPaintingData());
        entity.setId(UUID.fromString(request.getId().toStringUtf8()));
        return entity;
    }

    @Nonnull
    public static PaintingEntity fromAddMuseumGrpcMessage(@Nonnull AddPaintingRequest request) {
        UUID museumId = UUID.fromString(request.getMuseumId().getId().toStringUtf8());
        UUID artistId = UUID.fromString(request.getArtistId().getId().toStringUtf8());

        PaintingEntity entity = new PaintingEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setContent(request.getContent().toByteArray());
        entity.setMuseumId(museumId);
        entity.setArtistId(artistId);
        return entity;
    }

    @Nonnull
    public static PaintingResponse toGrpcMessage(@Nonnull PaintingEntity paintingEntity) {
        return PaintingResponse.newBuilder()
                .setId(copyFromUtf8(paintingEntity.getId().toString()))
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setContent(copyFrom(paintingEntity.getContent()))
                .setArtistId(ArtistId.newBuilder().setId(copyFromUtf8(paintingEntity.getArtistId().toString())))
                .setMuseumId(MuseumId.newBuilder().setId(copyFromUtf8(paintingEntity.getMuseumId().toString())))
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
        PaintingEntity that = (PaintingEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass().hashCode() : getClass().hashCode();
    }
}
