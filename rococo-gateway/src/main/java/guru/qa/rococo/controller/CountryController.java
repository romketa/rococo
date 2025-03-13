package guru.qa.rococo.controller;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.GrpcCountryClient;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/country")
public class CountryController {

    private final GrpcCountryClient countryClient;

    @Autowired
    public CountryController(GrpcCountryClient countryClient) {
        this.countryClient = countryClient;
    }

    @GetMapping()
    public Page<CountryJson> getAllCountries(@Nonnull Pageable pageable) {
        return countryClient.getAllCountries(pageable);
    }

    @GetMapping("/{id}")
    public CountryJson getCountry(@PathVariable UUID id) {
        return countryClient.getCountry(id);
    }

    @GetMapping("/{countryName}")
    public CountryJson getCountryByName(@PathVariable String countryName) {
        return countryClient.getCountryByName(countryName);
    }
}
