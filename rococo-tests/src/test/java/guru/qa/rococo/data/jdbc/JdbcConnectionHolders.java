package guru.qa.rococo.data.jdbc;

import java.util.List;
import javax.annotation.Nonnull;

public class JdbcConnectionHolders implements AutoCloseable {

  private final List<JdbcConnectionHolder> holders;

  public JdbcConnectionHolders(@Nonnull List<JdbcConnectionHolder> holders) {
    this.holders = holders;
  }

  @Override
  public void close() {
    holders.forEach(JdbcConnectionHolder::close);
  }
}
