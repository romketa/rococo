package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.impl.CountryRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.CountryJson;
import io.qameta.allure.Step;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class CountryDbClient {

  private final CountryRepository countryRepository = new CountryRepositoryHibernate();

  @Nonnull
  @Step("Find country by Id via DB")
  public CountryJson findCountryById(UUID id) {
    return CountryJson.fromEntity(countryRepository.findById(id).orElseThrow());
  }

  @Nonnull
  @Step("Find country by name via DB")
  public CountryJson findCountryByName(String name) {
    return CountryJson.fromEntity(countryRepository.findByName(name).orElseThrow());
  }

  @Nonnull
  @Step("Find all countries via DB")
  public List<CountryJson> getAllCountries() {
    return countryRepository.getAllCountries().stream()
        .map(CountryJson::fromEntity)
        .toList();
  }
}
