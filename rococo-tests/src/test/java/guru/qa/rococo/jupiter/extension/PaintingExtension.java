package guru.qa.rococo.jupiter.extension;

import static utils.RandomDataUtils.getRandomPainting;
import static utils.RandomDataUtils.randomDescription;
import static utils.RandomDataUtils.randomPaintingTitle;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.db.MuseumDbClient;
import guru.qa.rococo.service.db.PaintingDbClient;
import java.util.List;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import utils.ImageUtils;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      PaintingExtension.class);
  private final PaintingDbClient paintingDbClient = new PaintingDbClient();

  @Override
  public void beforeEach(ExtensionContext context) {

    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
        .ifPresent(paintingAnno -> {
          final String paintingTitle = "".equals(paintingAnno.title())
              ? randomPaintingTitle()
              : paintingAnno.title();
          final String paintingDescription = "".equals(paintingAnno.description())
              ? randomDescription()
              : paintingAnno.description();
          final String content = ImageUtils.convertImgToBase64(getRandomPainting());
          MuseumJson museum = context.getStore(MuseumExtension.NAMESPACE)
              .get(context.getUniqueId(), MuseumJson.class);
          ArtistJson artist = context.getStore(ArtistExtension.NAMESPACE)
              .get(context.getUniqueId(), ArtistJson.class);
          PaintingJson painting = new PaintingJson(
              null,
              paintingTitle,
              paintingDescription,
              content,
              museum,
              artist
          );
          PaintingJson createdPainting = paintingDbClient.createNewPainting(painting);
          PaintingJson completedPainting = completePaintingData(createdPainting, museum, artist);
          setPainting(completedPainting);
        });
  }


  public static void setPainting(PaintingJson paintings) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        paintings
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public PaintingJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), PaintingJson.class);
  }

  private PaintingJson completePaintingData(PaintingJson painting, MuseumJson museum, ArtistJson artist) {
    return new PaintingJson(
        painting.id(),
        painting.title(),
        painting.description(),
        painting.content(),
        museum,
        artist
    );
  }
}
