package guru.qa.rococo.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.model.allure.ScreenDif;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ScreenShotTestExtension.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Base64.Encoder encoder = Base64.getEncoder();

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(),
        ScreenShotTest.class) &&
        parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
  }

  @SneakyThrows
  @Override
  public BufferedImage resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return ImageIO.read(new ClassPathResource(
        extensionContext.getRequiredTestMethod().getAnnotation(ScreenShotTest.class)
            .value()).getInputStream());
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    ScreenDif screenDif = new ScreenDif(
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(getExpected())),
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(getActual())),
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(getDiff()))
    );

    Allure.addAttachment(
        "Screenshot diff",
        "application/vnd.allure.image.diff",
        objectMapper.writeValueAsString(screenDif)
    );
    ScreenShotTest annotation = context.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);
    if (annotation.rewriteExpected()) {
      String expectedPath = "src/test/resources/" + context.getRequiredTestMethod()
          .getAnnotation(ScreenShotTest.class).value();
      try {
        Files.write(Path.of(expectedPath), imageToBytes(getActual()));
        System.out.println("Expected file rewrote");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    throw throwable;
  }

  public static void setExpected(BufferedImage expected) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
  }

  public static BufferedImage getExpected() {
    return TestMethodContextExtension.context().getStore(NAMESPACE)
        .get("expected", BufferedImage.class);
  }

  public static void setActual(BufferedImage actual) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
  }

  public static BufferedImage getActual() {
    return TestMethodContextExtension.context().getStore(NAMESPACE)
        .get("actual", BufferedImage.class);
  }

  public static void setDiff(BufferedImage diff) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
  }

  public static BufferedImage getDiff() {
    return TestMethodContextExtension.context().getStore(NAMESPACE)
        .get("diff", BufferedImage.class);
  }

  private static byte[] imageToBytes(BufferedImage image) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
