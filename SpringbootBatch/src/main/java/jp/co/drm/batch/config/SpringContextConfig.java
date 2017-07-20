package jp.co.drm.batch.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SpringContextConfig {

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:ErrorResources", "classpath:ErrorResources2");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	// destroy-method="close"的作用是当数据库连接不使用的时候,就把该连接重新放到数据池中,方便下次使用调用.
	// WEB APP
	// バッチなら、エラー「springbatch Failed to unregister the JMX name
	// 原因：SpringでBasicDataSourceを二回目にクローズしようとした
	// 1.BasicDataSource close itself automatically when application close
	// 2.Spring use default destroy method to close DataSource but it's already
	// closed
	// @Bean(destroyMethod = "close")
	@Bean(destroyMethod = "") // バッチ 接続：１回のみ
	public DataSource dataSource() {
		// DBCP2のデータソースを使う
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(username);// 用户名
		dataSource.setPassword(password);// 密码
		dataSource.setDriverClassName(driverClassName);
		dataSource.setInitialSize(3);// 初始化时建立物理连接的个数
		dataSource.setMaxTotal(10);// 最大连接池数量
		dataSource.setMinIdle(3);// 最小连接池数量
		dataSource.setTimeBetweenEvictionRunsMillis(60000);// 获取连接时最大等待时间，单位毫秒。
		dataSource.setValidationQuery("SELECT 1 FROM DUAL");// 用来检测连接是否有效的sql
		dataSource.setTestOnBorrow(true);// 申请连接时执行validationQuery检测连接是否有效
		dataSource.setTestWhileIdle(true);// 建议配置为true，不影响性能，并且保证安全性。
		dataSource.setPoolPreparedStatements(false);// 是否缓存preparedStatement，也就是PSCache
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());

		return transactionManager;
	}

}
