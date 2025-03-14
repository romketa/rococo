package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.MuseumJson;
import io.qameta.allure.Step;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;

public class MuseumDbClient {

  private static final Config CFG = Config.getInstance();
  private final MuseumRepository museumRepository = new MuseumRepositoryHibernate();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.museumJdbcUrl()
  );

  @Nonnull
  @Step("Create museum via DB")
  public MuseumJson createNewMuseum(MuseumJson museum) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> MuseumJson.fromEntity(
            museumRepository.create(MuseumEntity.fromJson(museum))
        )
    ));
  }

  @Nonnull
  @Step("Update museum via DB")
  public MuseumJson updateMuseum(MuseumJson museum) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> MuseumJson.fromEntity(
            museumRepository.update(MuseumEntity.fromJson(museum))
        )
    ));
  }

  @Nonnull
  @Step("Find museum by Id via DB")
  public MuseumJson findMuseumById(UUID id) {
    return MuseumJson.fromEntity(museumRepository.findById(id).orElseThrow());
  }

  @Nonnull
  @Step("Find museum by title via DB")
  public MuseumJson findMuseumByTitle(String title) {
    return MuseumJson.fromEntity(museumRepository.findByTitle(title));
  }
}
