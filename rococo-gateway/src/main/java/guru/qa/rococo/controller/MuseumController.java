package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/museum")
public class MuseumController {

  private final MuseumClient museumClient;

  @Autowired
  public MuseumController(MuseumClient museumClient) {
    this.museumClient = museumClient;
  }

  @GetMapping("/museum")
  public List<MuseumJson> getMuseums() {
    return museumClient.getAllMuseums();
  }


  @GetMapping("/museum/{id}")
  public MuseumJson getMuseumById(@PathVariable UUID id) {
    return museumClient.getMuseumById(id);
  }

  @PatchMapping("/museum")
  public MuseumJson editMuseum(@RequestBody MuseumJson museumJson) {
    return museumClient.editMuseum(museumJson);
  }
}
