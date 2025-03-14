package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.repository.impl.PaintingRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.PaintingJson;
import io.qameta.allure.Step;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;

public class PaintingDbClient {

  private static final Config CFG = Config.getInstance();
  private final PaintingRepository paintingRepository = new PaintingRepositoryHibernate();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.paintingJdbcUrl()
  );

  @Nonnull
  @Step("Create painting via DB")
  public PaintingJson createNewPainting(PaintingJson painting) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> PaintingJson.fromEntity(
            paintingRepository.create(PaintingEntity.fromJson(painting))
        )
    ));
  }

  @Nonnull
  @Step("Update painting via DB")
  public PaintingJson updatePainting(PaintingJson painting) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> PaintingJson.fromEntity(
            paintingRepository.update(PaintingEntity.fromJson(painting))
        )
    ));
  }

  @Nonnull
  @Step("Find painting by Id via DB")
  public PaintingJson findPaintingById(UUID id) {
    return PaintingJson.fromEntity(paintingRepository.findById(id).orElseThrow());
  }
}
