package guru.qa.rococo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GeoJson{

    @JsonProperty("city")
    String city;
    @JsonProperty("country")
    CountryJson country;
}
