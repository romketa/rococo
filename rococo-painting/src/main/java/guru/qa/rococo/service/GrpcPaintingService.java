package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;
import static java.util.UUID.fromString;

@GrpcService
public class GrpcPaintingService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {

    private final PaintingRepository paintingRepository;

    public GrpcPaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    @Override
    public void getPainting(PaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        UUID paintingId = fromString(request.getId().toStringUtf8());
        paintingRepository.findById(paintingId)
                .ifPresentOrElse(
                        ae -> {
                            PaintingResponse response = PaintingEntity.toGrpcMessage(ae);
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Painting not found by id " + paintingId)
                                        .asRuntimeException()
                        )
                );
    }

    @Override
    public void getAllPaintings(AllPaintingsRequest request,
                              StreamObserver<AllPaintingsResponse> responseObserver) {
        int page = request.getPage();
        int size = request.getSize();
        PageRequest pageable = PageRequest.of(page, size);
        String title = request.getTitle();
        Page<PaintingEntity> paintingPage = paintingRepository.findByTitleContainingIgnoreCase(title, pageable);

        AllPaintingsResponse.Builder responseBuilder = AllPaintingsResponse.newBuilder();
        paintingPage.forEach(museumEntity -> {
            PaintingResponse museumResponse = PaintingEntity.toGrpcMessage(museumEntity);
            responseBuilder.addPaintings1(museumResponse);
        });
        responseBuilder.setPaintingsCount2((int) paintingPage.getTotalElements());

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void addPainting(AddPaintingRequest request,
                          StreamObserver<PaintingResponse> responseObserver) {
        PaintingEntity entity = paintingRepository.save(PaintingEntity.fromAddMuseumGrpcMessage(request));
        responseObserver.onNext(PaintingEntity.toGrpcMessage(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void editPainting(EditPaintingRequest request,
                           StreamObserver<PaintingResponse> responseObserver) {
        UUID paintingId = fromString(request.getId().toStringUtf8());
        paintingRepository.findById(paintingId)
                .ifPresentOrElse(
                        paintingEntity -> {
                            paintingEntity = PaintingEntity.fromEditPaintingGrpcMessage(request);
                            paintingRepository.save(paintingEntity);
                            responseObserver.onNext(PaintingEntity.toGrpcMessage(paintingEntity));
                            responseObserver.onCompleted();
                        }, () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Painting not found by id: " + paintingId)
                                        .asRuntimeException()
                        )
                );
    }

    public void getPaintingByArtist(PaintingByArtistRequest request, StreamObserver<AllPaintingsResponse> responseObserver) {
        int page = request.getPage();
        int size = request.getSize();
        PageRequest pageable = PageRequest.of(page, size);
        UUID artistId = fromString(request.getArtistId().toStringUtf8());

        Page<PaintingEntity> paintingPage = paintingRepository.findByArtistId(pageable, artistId);

        AllPaintingsResponse.Builder responseBuilder = AllPaintingsResponse.newBuilder();
        paintingPage.forEach(paintingEntity -> {
            PaintingResponse museumResponse = PaintingEntity.toGrpcMessage(paintingEntity);
            responseBuilder.addPaintings1(museumResponse);
        });
        responseBuilder.setPaintingsCount2((int) paintingPage.getTotalElements());

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
