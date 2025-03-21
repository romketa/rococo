package guru.qa.rococo.jupiter.extension;

import static utils.RandomDataUtils.getRandomCountry;

import guru.qa.rococo.jupiter.annotation.Country;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.db.CountryDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

public class CountryExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      CountryExtension.class);
  private final CountryDbClient countryDbClient = new CountryDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Country.class)
        .ifPresent(countryAnno -> {
          CountryJson country = "".equals(countryAnno.name())
              ? getRandomCountry()
              : countryDbClient.findCountryByName(countryAnno.name());
          setCountry(country);
        });
  }

  public static void setCountry(CountryJson country) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        country
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CountryJson.class);
  }

  @Override
  public CountryJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), CountryJson.class);
  }
}
