package bitcamp.myapp.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@ComponentScan(
    value = "bitcamp.myapp",
    excludeFilters = {
        @Filter(
            type = FilterType.REGEX,
            pattern = {"bitcamp.myapp.controller.*"})
    })
@PropertySource("classpath:/bitcamp/myapp/config/jdbc.properties")
@MapperScan("bitcamp.myapp.dao")
@EnableTransactionManagement
public class RootConfig {

  Logger log = LogManager.getLogger(getClass());

  {
    log.trace("RootConfig 생성됨!");
  }

  @Bean
  public DataSource dataSource(
      @Value("${jdbc.driver}") String jdbcDriver,
      @Value("${jdbc.url}") String url,
      @Value("${jdbc.username}") String username,
      @Value("${jdbc.password}") String password) {
    log.trace("DataSource 생성됨!");
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName(jdbcDriver);
    ds.setUrl(url);
    ds.setUsername(username);
    ds.setPassword(password);
    return ds;
  }

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) throws Exception {
    log.trace("PlatformTransactionManager 객체 생성! ");
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext appCtx) throws Exception {
    log.trace("SqlSessionFactory 객체 생성!");

    // Mybatis 로깅 기능을 활성화시킨다.
    org.apache.ibatis.logging.LogFactory.useLog4J2Logging();

    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);
    factoryBean.setTypeAliasesPackage("bitcamp.myapp.vo");
    factoryBean.setMapperLocations(appCtx.getResources("classpath*:bitcamp/myapp/mapper/*Mapper.xml"));
    return factoryBean.getObject();
  }
}








