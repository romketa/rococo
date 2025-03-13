package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.GrpcMuseumClient;
import jakarta.annotation.Nonnull;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/museum")
public class MuseumController {

  private final GrpcMuseumClient museumClient;

  @Autowired
  public MuseumController(GrpcMuseumClient museumClient) {
    this.museumClient = museumClient;
  }

  @GetMapping()
  public Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
    return museumClient.getAllMuseums(pageable);
  }

  @GetMapping("/{id}")
  public MuseumJson getMuseum(@PathVariable UUID id) {
    return museumClient.getMuseum(id);
  }

  @PatchMapping()
  public MuseumJson editMuseum(@RequestBody MuseumJson museumJson) {
    return museumClient.editMuseum(museumJson);
  }

  @PostMapping()
  public MuseumJson addMuseum(@RequestBody MuseumJson museumJson) {
    return museumClient.addMuseum(museumJson);
  }
}
