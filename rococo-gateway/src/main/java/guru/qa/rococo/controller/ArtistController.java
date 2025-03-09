package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.GrpcArtistClient;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/artist")
public class ArtistController {

  private final GrpcArtistClient grpcArtistClient;

  @Autowired
  public ArtistController(GrpcArtistClient grpcArtistClient) {
    this.grpcArtistClient = grpcArtistClient;
  }

  @GetMapping()
  public Page<ArtistJson> getArtists(@PageableDefault Pageable pageable) {
    return grpcArtistClient.getAllArtist(pageable);
  }


  @GetMapping("/{id}")
  public ArtistJson getArtistById(@PathVariable UUID id) {
    return grpcArtistClient.getArtist(id);
  }

  @PatchMapping()
  public ArtistJson editArtist(@RequestBody ArtistJson artistJson) {
    return grpcArtistClient.editArtist(artistJson);
  }

  @PostMapping()
  public ArtistJson addArtist(@RequestBody ArtistJson artistJson) {
    return grpcArtistClient.addArtist(artistJson);
  }
}
