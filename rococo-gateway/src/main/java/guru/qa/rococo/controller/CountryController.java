package guru.qa.rococo.controller;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.CountryClient;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/country")
public class CountryController {
  
  private final CountryClient countryClient;

  @Autowired
  public CountryController(CountryClient countryClient) {
    this.countryClient = countryClient;
  }

  @GetMapping()
  public Page<CountryJson> getCountries(@Nonnull Pageable pageable) {
    return countryClient.getAllCountries(pageable);
  }


  @GetMapping("/country/{id}")
  public CountryJson getCountryById(@PathVariable UUID id) {
    return countryClient.getCountryById(id);
  }

  @PatchMapping("/country")
  public CountryJson editCountry(@RequestBody CountryJson CountryJson) {
    return countryClient.editCountry(CountryJson);
  }
}
