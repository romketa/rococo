package guru.qa.rococo.jupiter.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import guru.qa.rococo.jupiter.extension.ArtistExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ArtistExtension.class)
public @interface Artist {

  String name() default "";

  String biography() default "";
}
