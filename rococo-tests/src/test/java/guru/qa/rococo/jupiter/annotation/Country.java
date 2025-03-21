package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.extension.CountryExtension;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(CountryExtension.class)
public @interface Country {

  String name() default "";
}
