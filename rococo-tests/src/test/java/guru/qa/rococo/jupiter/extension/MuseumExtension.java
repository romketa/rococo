package guru.qa.rococo.jupiter.extension;

import static utils.RandomDataUtils.getRandomCountry;
import static utils.RandomDataUtils.randomCity;
import static utils.RandomDataUtils.randomDescription;
import static utils.RandomDataUtils.randomMuseumTitle;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.db.CountryDbClient;
import guru.qa.rococo.service.db.MuseumDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import utils.ImageUtils;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      MuseumExtension.class);
  private final MuseumDbClient museumDbClient = new MuseumDbClient();
  private final CountryDbClient countryDbClient = new CountryDbClient();
  private static final String MUSEUM_PATH = "img/museums/ermitaj.jpg";

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
        .ifPresent(userAnno -> {
          final String city = "".equals(userAnno.city())
              ? randomCity()
              : userAnno.city();
          final CountryJson country = "".equals(userAnno.city())
              ? getRandomCountry()
              : countryDbClient.findCountryByName(userAnno.country());
          GeoJson geo = new GeoJson(city, country);
          MuseumJson museum = new MuseumJson(
              null,
              "".equals(userAnno.title()) ? randomMuseumTitle() : userAnno.title(),
              "".equals(userAnno.description()) ? randomDescription() : userAnno.description(),
              ImageUtils.convertImgToBase64(MUSEUM_PATH),
              geo
          );
          MuseumJson createdMuseum = museumDbClient.createNewMuseum(museum);
          setMuseum(createdMuseum);
        });
  }

  public static void setMuseum(MuseumJson museum) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        museum
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
  }

  @Override
  public MuseumJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), MuseumJson.class);
  }
}
