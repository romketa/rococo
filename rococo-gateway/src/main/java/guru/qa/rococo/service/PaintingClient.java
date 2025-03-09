package guru.qa.rococo.service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.EntityAlreadyExistsException;
import guru.qa.rococo.ex.MuseumNotFoundException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaintingClient {

  private final PaintingRepository paintingRepository;
  private final MuseumClient museumClient;
//  private final ArtistClient artistClient;

  private static final Logger LOG = LoggerFactory.getLogger(PaintingClient.class);

  @Autowired
  public PaintingClient(PaintingRepository paintingRepository, MuseumClient museumClient) {
//      ArtistClient artistClient) {
    this.paintingRepository = paintingRepository;
    this.museumClient = museumClient;
//    this.artistClient = artistClient;
  }

  @Nonnull
  public Page<PaintingJson> getAllPaintings(@Nonnull Pageable pageable) {
    Page<PaintingJson> painting = paintingRepository.findAll(pageable)
        .map(PaintingJson::fromEntity);
    List<PaintingJson> paintingList = painting.getContent();
    completePaintingData(paintingList);
    return new PageImpl<>(paintingList, pageable, painting.getTotalElements());
  }

  @Nonnull
  public Page<PaintingJson> getPaintingByArtist(@Nonnull Pageable pageable, @Nonnull UUID id) {
    Page<PaintingJson> painting = paintingRepository.findByArtistId(pageable, id)
        .map(PaintingJson::fromEntity);
    List<PaintingJson> paintingList = painting.getContent();
    completePaintingData(paintingList);
    return new PageImpl<>(paintingList, pageable, painting.getTotalElements());
  }

  @Nonnull
  public PaintingJson getPaintingById(@Nonnull UUID id) {
    PaintingJson painting = paintingRepository.findById(id)
        .map(PaintingJson::fromEntity)
        .orElseThrow(() -> new MuseumNotFoundException("Museum not found"));
    completePaintingData(painting);
    return painting;
  }

  @Nonnull
  public PaintingJson editPainting(@Nonnull PaintingJson painting) {
    PaintingJson paintingJson = paintingRepository.findById(painting.getId()).map(
        paintingEntity -> {
          paintingEntity.setId(painting.getId());
          paintingEntity.setTitle(painting.getTitle());
          paintingEntity.setDescription(painting.getDescription());
          paintingEntity.setContent(painting.getContent().getBytes(StandardCharsets.UTF_8));
          return PaintingJson.fromEntity(paintingRepository.save(paintingEntity));
        }
    ).orElseThrow(() -> new MuseumNotFoundException(
        "Can`t find painting by given id: " + painting.getId())
    );
    completePaintingData(painting);
    return paintingJson;
  }

  @Nonnull
  public PaintingJson addPainting(@Nonnull PaintingJson paintingJson) {
    PaintingJson painting = PaintingJson.fromEntity(this.save(paintingJson));
    completePaintingData(painting);
    return paintingJson;
  }

  @Nonnull
  @Transactional
  PaintingEntity save(@Nonnull PaintingJson painting) {
    final String title = painting.getTitle();

    paintingRepository.findByTitle(title).ifPresent(ae -> {
      LOG.error("### Can`t add already exist painting with title: {}", title);
      throw new EntityAlreadyExistsException(
          "Can`t add already exist painting with title: '" + title + "'");
    });

    PaintingEntity me = new PaintingEntity();
    me.setTitle(title);
    me.setDescription(painting.getDescription());
    me.setContent(painting.getContent().getBytes(StandardCharsets.UTF_8));
    me.setArtistId(painting.getArtist().id());
    me.setMuseumId(painting.getMuseum().id());
    return paintingRepository.save(me);
  }

  private void completePaintingData(@Nonnull PaintingJson painting) {
    painting.setMuseum(museumClient.getMuseumById(painting.getMuseum().id()));
//    painting.setArtist(artistClient.getArtistById(painting.getArtist().id()));
  }

  private void completePaintingData(@Nonnull List<PaintingJson> paintingList) {
    Set<UUID> museumIds = paintingList.stream()
            .map(painting -> painting.getMuseum().id())
            .collect(Collectors.toSet());
    Set<UUID> artistIds = paintingList.stream()
        .map(painting -> painting.getArtist().id())
        .collect(Collectors.toSet());

    List<MuseumJson> museums = museumClient.getMuseumByIds(museumIds);
//    List<ArtistJson> artists = artistClient.getArtistByIds(artistIds);

    paintingList.forEach(museum -> {
      UUID museumId = museum.getId();
      museums.stream()
          .filter(mj -> mj.id().equals(museumId))
          .findFirst()
          .ifPresent(museum::setMuseum);
    });

    paintingList.forEach(artist -> {
      UUID artistId = artist.getId();
//      artists.stream()
//          .filter(mj -> mj.id().equals(artistId))
//          .findFirst()
//          .ifPresent(artist::setArtist);
    });
  }
}
