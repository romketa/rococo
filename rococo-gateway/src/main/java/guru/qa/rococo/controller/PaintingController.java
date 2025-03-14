package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.GrpcPaintingClient;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/painting")
public class PaintingController {

  private final GrpcPaintingClient paintingClient;

  @Autowired
  public PaintingController(GrpcPaintingClient paintingClient) {
    this.paintingClient = paintingClient;
  }

  @GetMapping()
  public Page<PaintingJson> getAllPaintings(@RequestParam(required = false) String title, @PageableDefault Pageable pageable) {
    return paintingClient.getAllPaintings(title, pageable);
  }

  @GetMapping("/author/{id}")
  public Page<PaintingJson> getPaintingByArtist(@PageableDefault Pageable pageable,
      @PathVariable UUID id) {
    return paintingClient.getPaintingByArtist(pageable, id);
  }

  @GetMapping("/{id}")
  public PaintingJson getPainting(@PathVariable UUID id) {
    return paintingClient.getPainting(id);
  }

  @PostMapping
  public PaintingJson addPainting(@RequestBody PaintingJson paintingJson) {
    return paintingClient.addPainting(paintingJson);
  }

  @PatchMapping
  public PaintingJson editPainting(@RequestBody PaintingJson paintingJson) {
    return paintingClient.editPainting(paintingJson);
  }
}