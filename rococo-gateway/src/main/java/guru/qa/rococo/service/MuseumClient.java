package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.MuseumNotFoundException;
import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MuseumClient {

  MuseumRepository museumRepository;

  @Autowired
  public MuseumClient(MuseumRepository museumRepository) {
    this.museumRepository = museumRepository;
  }

  @Nonnull
  public Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
    return museumRepository.findAll(pageable)
        .map(MuseumJson::fromEntity);
  }

  @Nonnull
  public MuseumJson getMuseumById(UUID id) {
    return museumRepository.findById(id)
        .map(MuseumJson::fromEntity)
        .orElseThrow(() -> new MuseumNotFoundException("Museum not found"));
  }

  @Nonnull
  public MuseumJson editMuseum(MuseumJson museum) {
    return museumRepository.findByIdAndTitle(museum.id(), museum.title()).map(
        museumEntity -> {
          museumEntity.setId(museum.id());
          museumEntity.setTitle(museum.title());
          museumEntity.setDescription(museum.description());
          museumEntity.setPhoto(museum.photo().getBytes() );
          return MuseumJson.fromEntity(museumRepository.save(museumEntity));
        }
    ).orElseThrow(() -> new MuseumNotFoundException(
        "Can`t find museum by given id: " + museum.id()
    ));
  }

}
