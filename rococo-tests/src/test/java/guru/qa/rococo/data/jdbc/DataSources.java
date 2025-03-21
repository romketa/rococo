package guru.qa.rococo.data.jdbc;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;

public class DataSources {
  private DataSources() {
  }

  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  @Nonnull
  public static DataSource dataSource(@Nonnull String jdbcUrl) {
    return dataSources.computeIfAbsent(
        jdbcUrl,
        key -> {
          AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
          final String uniqId = StringUtils.substringAfter(jdbcUrl, "3306/");
          dsBean.setUniqueResourceName(uniqId);
          dsBean.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
          Properties props = new Properties();
          props.put("URL", jdbcUrl);
          props.put("user", "root");
          props.put("password", "secret");
          dsBean.setXaProperties(props);
          dsBean.setPoolSize(3);
          dsBean.setMaxPoolSize(20);
          P6DataSource p6DataSource = new P6DataSource(
              dsBean
          );
          try {
            InitialContext context = new InitialContext();
            context.bind("java:comp/env/jdbc/" + uniqId, p6DataSource);
          } catch (NamingException e) {
            throw new RuntimeException(e);
          }
          return p6DataSource;
        }
    );
  }
}
