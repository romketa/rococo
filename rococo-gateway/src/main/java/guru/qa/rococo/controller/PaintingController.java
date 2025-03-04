package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.PaintingClient;
import java.util.List;
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
@RequestMapping("api/painting")
public class PaintingController {

  private final PaintingClient paintingClient;

  @Autowired
  public PaintingController(PaintingClient paintingClient) {
    this.paintingClient = paintingClient;
  }

  @GetMapping()
  public Page<PaintingJson> getPaintings(@PageableDefault Pageable pageable) {
    return paintingClient.getAllPaintings(pageable);
  }


  @GetMapping("/author/{id}")
  public Page<PaintingJson> getPaintingByAuthor(@PageableDefault Pageable pageable,
      @PathVariable UUID id) {
    return paintingClient.getPaintingByArtistId(pageable, id);
  }

  @GetMapping("/{id}")
  public PaintingJson getPaintingById(@PathVariable UUID id) {
    return paintingClient.getPaintingById(id);
  }

  @PostMapping
  public PaintingJson addPainting(@RequestBody PaintingJson paintingJson) {
    return paintingClient.addPainting(paintingJson);
  }
}