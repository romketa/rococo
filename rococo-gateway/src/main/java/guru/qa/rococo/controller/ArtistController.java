package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/artist")
public class ArtistController {

  private final ArtistClient artistClient;

  @Autowired
  public ArtistController(ArtistClient artistClient) {
    this.artistClient = artistClient;
  }

  @GetMapping("/")
  public List<ArtistJson> getArtists() {
    return artistClient.getAllArtists();
  }


  @GetMapping("/{id}")
  public ArtistJson getArtistById(@PathVariable UUID id) {
    return artistClient.getArtistById(id);
  }

  @PatchMapping("/")
  public ArtistJson editArtist(@RequestBody ArtistJson artistJson) {
    return artistClient.editArtist(artistJson);
  }
}
