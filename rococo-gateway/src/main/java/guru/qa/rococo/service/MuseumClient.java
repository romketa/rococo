package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.EntityAlreadyExistsException;
import guru.qa.rococo.ex.MuseumNotFoundException;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MuseumClient {

  MuseumRepository museumRepository;
  CountryClient countryClient;
  private static final Logger LOG = LoggerFactory.getLogger(MuseumClient.class);

  @Autowired
  public MuseumClient(MuseumRepository museumRepository, CountryClient countryClient) {
    this.museumRepository = museumRepository;
    this.countryClient = countryClient;
  }

  @Nonnull
  public Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
    return museumRepository.findAll(pageable)
        .map(museumEntity -> MuseumJson.fromEntity(museumEntity,
            getCountryById(museumEntity)));
  }

  @Nonnull
  private CountryJson getCountryById(MuseumEntity museumEntity) {
    return countryClient.getCountryById(museumEntity.getCountryId());
  }

  @Nonnull
  public MuseumJson getMuseumById(UUID id) {
    return museumRepository.findById(id)
        .map(museumEntity -> MuseumJson.fromEntity(museumEntity,
            getCountryById(museumEntity)))
        .orElseThrow(() -> new MuseumNotFoundException("Museum not found"));
  }

  @Nonnull
  public MuseumJson editMuseum(MuseumJson museum) {
    return museumRepository.findById(museum.id()).map(
        museumEntity -> {
          museumEntity.setId(museum.id());
          museumEntity.setTitle(museum.title());
          museumEntity.setDescription(museum.description());
          museumEntity.setCity(museum.geo().city());
          museumEntity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
          return MuseumJson.fromEntity(museumRepository.save(museumEntity),
              getCountryById(museumEntity));
        }
    ).orElseThrow(() -> new MuseumNotFoundException(
        "Can`t find museum by given id: " + museum.id()
    ));
  }

  @Nonnull
  public MuseumJson addMuseum(MuseumJson museumJson) {
    return MuseumJson.fromEntity(this.save(museumJson), museumJson.geo().country());
  }

  @Nonnull
  @Transactional
  MuseumEntity save(@Nonnull MuseumJson museum) {
    final String title = museum.title();

    museumRepository.findByTitle(title).ifPresent(ae -> {
      LOG.error("### Can`t add already exist museum with title: {}", title);
      throw new EntityAlreadyExistsException(
          "Can`t add already exist museum with title: '" + title + "'");
    });
    MuseumEntity me = new MuseumEntity();
    me.setTitle(title);
    me.setPhoto(museum.photo().getBytes());
    me.setDescription(museum.description());
    me.setCity(museum.geo().city());
    me.setCountryId(museum.geo().country().id());
    return museumRepository.save(me);
  }

  @Nonnull
  private static CountryEntity getCountryEntity(MuseumJson museum) {
    CountryEntity ce = new CountryEntity();
    ce.setId(museum.geo().country().id());
    ce.setName(museum.geo().country().name());
    return ce;
  }
}
