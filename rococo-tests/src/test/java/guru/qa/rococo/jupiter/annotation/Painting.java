package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.extension.PaintingExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(PaintingExtension.class)
public @interface Painting {

  String title() default "";

  String description() default "";

  Artist artist() default @Artist;

  Museum museum() default @Museum;
}
