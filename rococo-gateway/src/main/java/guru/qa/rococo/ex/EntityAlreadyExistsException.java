package guru.qa.rococo.ex;

public class EntityAlreadyExistsException extends RuntimeException {

  public EntityAlreadyExistsException(String message) {
    super(message);
  }
}
