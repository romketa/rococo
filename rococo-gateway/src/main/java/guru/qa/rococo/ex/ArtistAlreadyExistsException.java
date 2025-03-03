package guru.qa.rococo.ex;

public class ArtistAlreadyExistsException extends RuntimeException {

  public ArtistAlreadyExistsException(String message) {
    super(message);
  }
}
