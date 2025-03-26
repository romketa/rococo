package guru.qa.rococo.service;

import static io.grpc.Status.NOT_FOUND;
import static java.util.UUID.fromString;

import guru.qa.grpc.rococo.AddPaintingRequest;
import guru.qa.grpc.rococo.AllPaintingsRequest;
import guru.qa.grpc.rococo.AllPaintingsResponse;
import guru.qa.grpc.rococo.EditPaintingRequest;
import guru.qa.grpc.rococo.PaintingByArtistRequest;
import guru.qa.grpc.rococo.PaintingRequest;
import guru.qa.grpc.rococo.PaintingResponse;
import guru.qa.grpc.rococo.RococoPaintingServiceGrpc;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.model.LogJson;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.kafka.core.KafkaTemplate;

@GrpcService
public class GrpcPaintingService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcPaintingService.class);
  private final PaintingRepository paintingRepository;
  private final KafkaTemplate<String, LogJson> kafkaTemplate;

  public GrpcPaintingService(PaintingRepository paintingRepository, KafkaTemplate<String, LogJson> kafkaTemplate) {
    this.paintingRepository = paintingRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void getPainting(PaintingRequest request,
      StreamObserver<PaintingResponse> responseObserver) {
    UUID paintingId = fromString(request.getId().toStringUtf8());
    paintingRepository.findById(paintingId)
        .ifPresentOrElse(
            ae -> {
              PaintingResponse response = PaintingEntity.toGrpcMessage(ae);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            },
            () -> responseObserver.onError(
                NOT_FOUND.withDescription("Painting not found by id: " + paintingId)
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
    Page<PaintingEntity> paintingPage = paintingRepository.findByTitleContainingIgnoreCase(title,
        pageable);

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
    PaintingEntity entity = paintingRepository.save(
        PaintingEntity.fromAddMuseumGrpcMessage(request));
    responseObserver.onNext(PaintingEntity.toGrpcMessage(entity));
    responseObserver.onCompleted();
    LogJson log = new LogJson(
        "Painting",
        entity.getId(),
        "Painting " + entity.getTitle() + " was successfully added",
        EventType.NEW_ENTITY,
        LocalDateTime.now()
    );
    kafkaTemplate.send("paintings", log);
    LOGGER.info("### Kafka topic [paintings] sent message: {} {}", entity.getTitle(),
        EventType.NEW_ENTITY);
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

              LogJson log = new LogJson(
                  "Painting",
                  paintingEntity.getId(),
                  "Painting " + paintingEntity.getTitle() + " was successfully updated",
                  EventType.EDIT_ENTITY,
                  LocalDateTime.now()
              );
              kafkaTemplate.send("paintings", log);
              LOGGER.info("### Kafka topic [paintings] sent message: {} {}", paintingEntity.getTitle(),
                  EventType.EDIT_ENTITY);
            }, () -> responseObserver.onError(
                NOT_FOUND.withDescription("Painting not found by id: " + paintingId)
                    .asRuntimeException()
            )
        );
  }

  public void getPaintingByArtist(PaintingByArtistRequest request,
      StreamObserver<AllPaintingsResponse> responseObserver) {
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
