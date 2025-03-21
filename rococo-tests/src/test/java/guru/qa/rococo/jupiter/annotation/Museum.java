package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.extension.MuseumExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(MuseumExtension.class)
public @interface Museum {

  String title() default "";

  String description() default "";

  String city() default "";

  String country() default "";
}
