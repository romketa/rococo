package guru.qa.rococo.jupiter.extension;


import guru.qa.rococo.data.jpa.EntityManagers;

public class DatabasesExtension implements SuiteExtension {
  @Override
  public void afterSuite() {
    EntityManagers.closeAllEmfs();
  }
}
