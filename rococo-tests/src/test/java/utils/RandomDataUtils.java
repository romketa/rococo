package utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.impl.CountryRepositoryHibernate;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.db.CountryDbClient;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

public class RandomDataUtils {

  private static final Faker fakeData = new Faker();

  @Nonnull
  public static String randomUsername() {
    return fakeData.name().username();
  }

  @Nonnull
  public static String shortUsername() {
    return "u";
  }

  @Nonnull
  public static String longUsername() {
    return "long_dddddddddddddddddddddddddddddddddsasdsadasddssdsd";
  }

  @Nonnull
  public static String randomName() {
    return fakeData.name().firstName();
  }

  @Nonnull
  public static String randomLastName() {
    return fakeData.name().lastName();
  }

  @Nonnull
  public static String randomArtistName() {
    return fakeData.artist().name() + "-" + fakeData.harryPotter().character();
  }

  @Nonnull
  public static String randomLongValue() {
    return fakeData.letterify(
        "?????????????????????????????????????????????????????????????????????"
            + "?????????????????????????????????????????????????????????????????????????"
            + "?????????????????????????????????????????????????????????????????????????"
            + "?????????????????????????????????????????");
  }

  @Nonnull
  public static String extraLongValue() {
    return "aasdsaasdadsasdadsasdasdasdasdadsdasasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdaasdsaasdadsasdadsasdasdas"
        + "dasdadsdasasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "daasdsaasdadsasdadsasdasdasdasdadsdasasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasda"
        + "sdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdaasdsaasdadsasdadsasdasdasdasdadsdasasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas"
        + "dasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"
        + "asdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddaasdasdasdasdasasdasd";
  }

  @Nonnull
  public static String randomMuseumTitle() {
    return fakeData.aviation().airport() + "-" + fakeData.animal().name();
  }

  @Nonnull
  public static String randomPaintingTitle() {
    return fakeData.starTrek().location() + "-" + fakeData.gameOfThrones().dragon();
  }

  @Nonnull
  public static String randomDescription() {
    return fakeData.letterify("???????????????????????????");
  }

  @Nonnull
  public static String randomCity() {
    return fakeData.address().cityName();
  }

  public static CountryJson getRandomCountry() {
    CountryDbClient countryDbClient = new CountryDbClient();

    List<CountryJson> countryName = countryDbClient.getAllCountries();
    Random random = new Random();
    int randomIndex = random.nextInt(countryName.size());
    return countryName.get(randomIndex);
  }

  public static String getRandomPainting() {
    List<String> paintingsPathList = List.of("img/paintings/scream.jpg", "img/paintings/sleep.jpg",
        "img/paintings/start-nigh.jpg");
    Random random = new Random();
    int randomIndex = random.nextInt(paintingsPathList.size());
    return paintingsPathList.get(randomIndex);
  }
}
