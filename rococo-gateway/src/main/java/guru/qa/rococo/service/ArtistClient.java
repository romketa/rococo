package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.model.ArtistJson;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtistClient {

  ArtistRepository artistRepository;

  @Autowired
  public ArtistClient(ArtistRepository artistRepository) {
    this.artistRepository = artistRepository;
  }

//  public List<ArtistJson> allArtists() {
//    return artistRepository.findAll()
//        .stream()
//        .map(ArtistJson::);
//  }
}
