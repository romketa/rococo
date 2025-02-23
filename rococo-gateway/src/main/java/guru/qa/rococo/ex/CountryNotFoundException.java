package guru.qa.rococo.ex;

public class CountryNotFoundException extends RuntimeException {

  public CountryNotFoundException(String message) {
    super(message);
  }
}
