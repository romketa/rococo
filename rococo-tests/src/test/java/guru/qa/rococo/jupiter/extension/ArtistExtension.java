package guru.qa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.db.ArtistDbClient;
import utils.ImageUtils;

import static utils.RandomDataUtils.randomArtistName;
import static utils.RandomDataUtils.randomDescription;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ArtistExtension.class);
  private final ArtistDbClient artistDbClient = new ArtistDbClient();
  private static final String ARTIST_PATH = "img/artists/repin.jpg";

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
        .ifPresent(artistAnno -> {
          ArtistJson artist = new ArtistJson(
              null,
              "".equals(artistAnno.name()) ? randomArtistName() : artistAnno.name(),
              "".equals(artistAnno.biography()) ? randomDescription() : artistAnno.biography(),
              ImageUtils.convertImgToBase64(ARTIST_PATH)
          );
          ArtistJson createdArtist = artistDbClient.createNewArtist(artist);
          setArtist(createdArtist);
        });
  }

  public static void setArtist(ArtistJson artist) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        artist
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
  }

  @Override
  public ArtistJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), ArtistJson.class);
  }
}
