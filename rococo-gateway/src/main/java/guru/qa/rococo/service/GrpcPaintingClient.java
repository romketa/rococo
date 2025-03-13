package guru.qa.rococo.service;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.model.PaintingJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.protobuf.ByteString.copyFromUtf8;

@Component
public class GrpcPaintingClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingClient.class);

    private final RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingStub;
    private final GrpcMuseumClient museumClient;
    private final GrpcArtistClient artistClient;

    @Autowired
    public GrpcPaintingClient(RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingStub,
                              GrpcMuseumClient museumClient,
                              GrpcArtistClient artistClient) {
        this.paintingStub = paintingStub;
        this.museumClient = museumClient;
        this.artistClient = artistClient;
    }

    public @Nonnull PaintingJson getPainting(@Nonnull UUID id) {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(copyFromUtf8(id.toString()))
                .build();
        try {
            PaintingResponse response = paintingStub.getPainting(request);
            PaintingJson painting = PaintingJson.fromGrpcMessage(response);
            completePaintingData(painting);
            return painting;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Can`t find painting by id " + id, e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "The gRPC operation was cancelled", e);
            }
        }
    }

    public @Nonnull Page<PaintingJson> getAllPaintings(@Nonnull Pageable pageable) {
        AllPaintingsRequest.Builder builder = AllPaintingsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize());
        AllPaintingsRequest request = builder.build();

        try {
            AllPaintingsResponse response = paintingStub.getAllPaintings(request);
            List<PaintingJson> paintingJsons = response.getPaintings1List()
                    .stream()
                    .map(PaintingJson::fromGrpcMessage)
                    .toList();
            completePaintingData(paintingJsons);
            return new PageImpl<>(paintingJsons, pageable, response.getPaintingsCount2());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull PaintingJson addPainting(@Nonnull PaintingJson painting) {
        AddPaintingRequest request = PaintingJson.toGrpcMessage(painting);
        PaintingResponse response = paintingStub.addPainting(request);
        PaintingJson addedPainting = PaintingJson.fromGrpcMessage(response);
        completePaintingData(addedPainting);
        return addedPainting;
    }

    public @Nonnull PaintingJson editPainting(@Nonnull PaintingJson painting) {
        AddPaintingRequest paintingData = PaintingJson.toGrpcMessage(painting);
        EditPaintingRequest request = EditPaintingRequest.newBuilder()
                .setId(ByteString.copyFromUtf8(painting.getId().toString()))
                .setPaintingData(paintingData)
                .build();
        PaintingResponse response = paintingStub.editPainting(request);
        PaintingJson changedPainting = PaintingJson.fromGrpcMessage(response);
        completePaintingData(changedPainting);
        return changedPainting;
    }

    public @Nonnull Page<PaintingJson> getPaintingByArtist(@Nonnull Pageable pageable, @Nonnull UUID id) {
        PaintingByArtistRequest.Builder builder = PaintingByArtistRequest.newBuilder()
                .setArtistId(copyFromUtf8(id.toString()))
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize());
        PaintingByArtistRequest request = builder.build();

        try {
            AllPaintingsResponse response = paintingStub.getPaintingByArtist(request);
            List<PaintingJson> paintingJsons = response.getPaintings1List()
                    .stream()
                    .map(PaintingJson::fromGrpcMessage)
                    .toList();
            completePaintingData(paintingJsons);
            return new PageImpl<>(paintingJsons, pageable, response.getPaintingsCount2());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The gRPC operation was cancelled", e);
        }
    }

    private void completePaintingData(@Nonnull PaintingJson painting) {
        painting.setMuseum(museumClient.getMuseum(painting.getMuseum().id()));
        painting.setArtist(artistClient.getArtist(painting.getArtist().id()));
    }

    private void completePaintingData(@Nonnull List<PaintingJson> paintingList) {
        Set<UUID> museumIds = paintingList.stream()
                .map(painting -> painting.getMuseum().id())
                .collect(Collectors.toSet());
        Set<UUID> artistIds = paintingList.stream()
                .map(painting -> painting.getArtist().id())
                .collect(Collectors.toSet());

        List<MuseumJson> museums = museumClient.getMuseumByIds(museumIds);
        List<ArtistJson> artists = artistClient.getArtistByIds(artistIds);

        paintingList.forEach(museum -> {
            UUID museumId = museum.getId();
            museums.stream()
                    .filter(mj -> mj.id().equals(museumId))
                    .findFirst()
                    .ifPresent(museum::setMuseum);
        });

        paintingList.forEach(artist -> {
            UUID artistId = artist.getId();
            artists.stream()
                    .filter(mj -> mj.id().equals(artistId))
                    .findFirst()
                    .ifPresent(artist::setArtist);
        });
    }
}
