package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.EntityAlreadyExistsException;
import guru.qa.rococo.ex.ArtistNotFoundException;
import guru.qa.rococo.model.ArtistJson;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArtistClient {

  ArtistRepository artistRepository;
  private static final Logger LOG = LoggerFactory.getLogger(ArtistClient.class);


  @Autowired
  public ArtistClient(ArtistRepository artistRepository) {
    this.artistRepository = artistRepository;
  }

  @Nonnull
  public Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable) {
    return artistRepository.findAll(pageable)
        .map(ArtistJson::fromEntity);
  }

  @Nonnull
  public ArtistJson getArtistById(UUID id) {
    return artistRepository.findById(id)
        .map(ArtistJson::fromEntity)
        .orElseThrow(() -> new ArtistNotFoundException("Artist not found"));
  }

  @Nonnull
  public ArtistJson editArtist(ArtistJson artist) {
    return artistRepository.findByIdAndName(artist.id(), artist.name()).map(
        artistEntity -> {
          artistEntity.setId(artist.id());
          artistEntity.setName(artist.name());
          artistEntity.setBiography(artist.biography());
          artistEntity.setPhoto(artist.photo().getBytes());
          return ArtistJson.fromEntity(artistRepository.save(artistEntity));
        }
    ).orElseThrow(() -> new ArtistNotFoundException(
        "Can`t find artist by given id: " + artist.id()
    ));
  }

  @Nonnull
  public ArtistJson addArtist(ArtistJson artistJson) {
    return ArtistJson.fromEntity(this.save(artistJson));
  }

  @Nonnull
  @Transactional
  ArtistEntity save(@Nonnull ArtistJson artist) {
    final String artistName = artist.name();

    artistRepository.findByName(artistName).ifPresent(ae -> {
      LOG.error("### Can`t add already exist artist with name: {}", artistName);
      throw new EntityAlreadyExistsException("Can`t add already exist artist with name: '" + artistName + "'");
    });

    ArtistEntity ae = new ArtistEntity();
    ae.setName(artist.name());
    ae.setPhoto(artist.photo().getBytes());
    ae.setBiography(artist.biography());
    return artistRepository.save(ae);
  }
}