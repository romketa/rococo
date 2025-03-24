package guru.qa.rococo.exception;

import java.io.Serial;
import org.openqa.selenium.manager.SeleniumManager;

public class BrowserTypeNotSupportedException extends Exception{

    @Serial
    private static final long serialVersionUID = -2647456118405954358L;

    public BrowserTypeNotSupportedException(SeleniumManager driverType) {
        super(String.format("Browser type %s doesn't support", driverType.toString()));
    }

    public BrowserTypeNotSupportedException(String driverType) {
        super(String.format("Browser type %s doesn't support", driverType));
    }
}
