package guru.qa.rococo.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.SneakyThrows;

public class AllureBackendLogsExtension implements SuiteExtension {

  public static final String caseName = "Rococo backend logs";

  @SneakyThrows
  @Override
  public void afterSuite() {
    final AllureLifecycle allureLifecycle = Allure.getLifecycle();
    final String caseId = UUID.randomUUID().toString();
    allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
    allureLifecycle.startTestCase(caseId);

    addAttachment("Rococo-auth log", "./logs/rococo-auth/app.log");
    addAttachment("Rococo-auth log", "./logs/rococo-auth/app.log");
    addAttachment("Rococo-gateway log", "./logs/rococo-gateway/app.log");
    addAttachment("Rococo-artist log", "./logs/rococo-artist/app.log");
    addAttachment("Rococo-country log", "./logs/rococo-country/app.log");
    addAttachment("Rococo-museum log", "./logs/rococo-museum/app.log");
    addAttachment("Rococo-painting log", "./logs/rococo-painting/app.log");

    allureLifecycle.stopTestCase(caseId);
    allureLifecycle.writeTestCase(caseId);
  }

  @SneakyThrows
  private void addAttachment(String name, String path) {
    Allure.getLifecycle()
        .addAttachment(name, "text/html", ".log", Files.newInputStream(Path.of(path)));
  }
}
