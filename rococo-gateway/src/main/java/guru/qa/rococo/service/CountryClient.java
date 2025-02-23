package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.ex.ArtistNotFoundException;
import guru.qa.rococo.ex.CountryNotFoundException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.CountryJson;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountryClient {

  CountryRepository countryRepository;

  @Autowired
  public CountryClient(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @Nonnull
  public List<CountryJson> getAllCountries() {
    return countryRepository.findAll()
        .stream()
        .map(CountryJson::fromEntity)
        .toList();
  }

  @Nonnull
  public CountryJson getCountryById(UUID id) {
    return countryRepository.findById(id)
        .map(CountryJson::fromEntity)
        .orElseThrow(() -> new CountryNotFoundException("Country not found"));
  }

  @Nonnull
  public CountryJson editCountry(CountryJson country) {
    return countryRepository.findByIdAndName(country.id(), country.name()).map(
        countryEntity -> {
          countryEntity.setId(country.id());
          countryEntity.setName(country.name());
          return CountryJson.fromEntity(countryRepository.save(countryEntity));
        }
    ).orElseThrow(() -> new CountryNotFoundException(
        "Can`t find country by given id: " + country.id()
    ));
  }

}
