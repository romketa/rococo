package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.MuseumNotFoundException;
import guru.qa.rococo.model.PaintingJson;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PaintingClient {

  PaintingRepository paintingRepository;

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
  public Page<PaintingJson> getPaintingByArtist(@Nonnull Pageable pageable, @Nonnull UUID id) {
    return paintingRepository.findByArtist(pageable, id)
        .map(PaintingJson::fromEntity);
  }

  @Nonnull
  public PaintingJson getPaintingById(@Nonnull UUID id) {
    return paintingRepository.findById(id)
        .map(PaintingJson::fromEntity)
        .orElseThrow(() -> new MuseumNotFoundException("Museum not found"));
  }
}
