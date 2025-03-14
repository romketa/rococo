package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.impl.ArtistRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.ArtistJson;
import io.qameta.allure.Step;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ArtistDbClient {

  private static final Config CFG = Config.getInstance();
  private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.artistJdbcUrl()
  );

  @Nonnull
  @Step("Create artist via DB")
  public ArtistJson createNewArtist(ArtistJson artist) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> ArtistJson.fromEntity(
            artistRepository.create(ArtistEntity.fromJson(artist))
        )
    ));
  }

  @Nonnull
  @Step("Update artist via DB")
  public ArtistJson updateArtist(ArtistJson artist) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(
        () -> ArtistJson.fromEntity(
            artistRepository.update(ArtistEntity.fromJson(artist))
        )
    ));
  }

  @Nonnull
  @Step("Find artist by Id via DB")
  public ArtistJson findArtistById(UUID id) {
    return ArtistJson.fromEntity(artistRepository.findById(id).orElseThrow());
  }
}
