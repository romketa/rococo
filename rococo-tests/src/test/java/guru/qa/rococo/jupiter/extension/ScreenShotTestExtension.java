package guru.qa.rococo.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.label.Env;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.model.allure.ScreenDif;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ScreenShotTestExtension.class);
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ScreenShotTestExtension.class);

  private static final Config CFG = Config.getInstance();
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
    final ScreenShotTest screenShotTest = extensionContext.getRequiredTestMethod()
        .getAnnotation(ScreenShotTest.class);
    return ImageIO.read(new ClassPathResource(
        Env.LOCAL.equals(CFG.env())
            ? "/img/local/" + screenShotTest.value()
            : "/img/selenoid/" + screenShotTest.value()
    ).getInputStream());
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    BufferedImage expected = getExpected();
    BufferedImage actual = getActual();
    BufferedImage diff = getDiff();

    // Проверяем, что изображения не null
    if (expected == null || actual == null || diff == null) {
      throw throwable; // Продолжаем выбрасывать исключение, если изображения отсутствуют
    }

    ScreenDif screenDif = new ScreenDif(
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(expected)),
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(actual)),
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(diff))
    );

    Allure.addAttachment(
        "Screenshot diff",
        "application/vnd.allure.image.diff",
        objectMapper.writeValueAsString(screenDif)
    );
    ScreenShotTest annotation = context.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);

    if (annotation.rewriteExpected()) {
      final String path = Env.LOCAL.equals(CFG.env())
          ? "rococo-tests/src/test/resources/img/local/"
          : "rococo-tests/src/test/resources/img/selenoid/";
      String expectedPath = path + annotation.value();
      try {
        Files.write(Path.of(expectedPath), imageToBytes(actual));
        LOG.info("Expected file rewrote {}", expectedPath);
      } catch (IOException e) {
        LOG.error("Cannot find or write the file by path {}", expectedPath);
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
