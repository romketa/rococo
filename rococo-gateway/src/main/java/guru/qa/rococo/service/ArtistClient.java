package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.ArtistNotFoundException;
import guru.qa.rococo.model.ArtistJson;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtistClient {

  ArtistRepository artistRepository;


  @Autowired
  public ArtistClient(ArtistRepository artistRepository) {
    this.artistRepository = artistRepository;
  }

  @Nonnull
  public List<ArtistJson> getAllArtists() {
    return artistRepository.findAll()
        .stream()
        .map(ArtistJson::fromEntity)
        .toList();
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
          artistEntity.setPhoto(artist.photo());
          return ArtistJson.fromEntity(artistRepository.save(artistEntity));
        }
    ).orElseThrow(() -> new ArtistNotFoundException(
        "Can`t find artist by given id: " + artist.id()
    ));
  }
}
