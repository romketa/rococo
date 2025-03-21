package guru.qa.rococo.jupiter.annotation.meta;

import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.jupiter.extension.CountryExtension;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import guru.qa.rococo.jupiter.extension.PaintingExtension;
import guru.qa.rococo.jupiter.extension.UserExtension;
import io.qameta.allure.junit5.AllureJunit5;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
    AllureJunit5.class,
    UserExtension.class,
    ArtistExtension.class,
    MuseumExtension.class,
    PaintingExtension.class,
    CountryExtension.class,
})
public @interface GrpcTest {

}
