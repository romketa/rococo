package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.MuseumEntity;
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
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaintingClient {

  PaintingRepository paintingRepository;
  private static final Logger LOG = LoggerFactory.getLogger(PaintingClient.class);

  @Autowired
  public PaintingClient(PaintingRepository paintingRepository) {
    this.paintingRepository = paintingRepository;
  }

  @Nonnull
  public Page<PaintingJson> getAllPaintings(@Nonnull Pageable pageable) {
    return paintingRepository.findAll(pageable)
        .map(PaintingJson::fromEntity);
  }

  @Nonnull
  public Page<PaintingJson> getPaintingByArtistId(@Nonnull Pageable pageable, @Nonnull UUID id) {
    return paintingRepository.findByArtistId(pageable, id)
        .map(PaintingJson::fromEntity);
  }

  @Nonnull
  public PaintingJson getPaintingById(@Nonnull UUID id) {
    return paintingRepository.findById(id)
        .map(PaintingJson::fromEntity)
        .orElseThrow(() -> new MuseumNotFoundException("Museum not found"));
  }

  @Nonnull
  public PaintingJson editPainting(@Nonnull PaintingJson painting) {
    return paintingRepository.findById(painting.id()).map(
            paintingEntity -> {
              paintingEntity.setId(painting.id());
              paintingEntity.setTitle(painting.title());
              paintingEntity.setDescription(painting.description());
              paintingEntity.setPhoto(painting.content().getBytes(StandardCharsets.UTF_8));
              return PaintingJson.fromEntity(paintingRepository.save(paintingEntity),
                      getCountryById(paintingEntity));
            }
    ).orElseThrow(() -> new MuseumNotFoundException(
            "Can`t find painting by given id: " + museum.id()
    ));
  }

  @Nonnull
  public PaintingJson addPainting(@Nonnull PaintingJson paintingJson) {
    return PaintingJson.fromEntity(this.save(paintingJson));
  }

  @Nonnull
  @Transactional
  PaintingEntity save(@Nonnull PaintingJson painting) {
    final String title = painting.title();

    paintingRepository.findByTitle(title).ifPresent(ae -> {
      LOG.error("### Can`t add already exist painting with title: {}", title);
      throw new EntityAlreadyExistsException("Can`t add already exist painting with title: '" + title + "'");
    });

    PaintingEntity me = new PaintingEntity();
    me.setTitle(title);
    me.setDescription(painting.description());
    me.setContent(painting.content().getBytes(StandardCharsets.UTF_8));
    me.setArtistId(painting.artistJson().id());
    me.setMuseumId(painting.museumJson().id());
    return paintingRepository.save(me);
  }
}
