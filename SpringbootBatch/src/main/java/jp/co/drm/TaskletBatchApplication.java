package jp.co.drm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication(scanBasePackages={"com.sample.commons", "com.sample.product"})
@SpringBootApplication(scanBasePackages = { "jp.co.drm" }) // デフォルト：当クラスのpackage
@MapperScan("jp.co.drm.**.integration.mybatis.dao")
@EnableBatchProcessing
public class TaskletBatchApplication {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	MessageSource msg;

	/*// destroy-method="close"的作用是当数据库连接不使用的时候,就把该连接重新放到数据池中,方便下次使用调用.
	//WEB APP
	// バッチなら、エラー「springbatch Failed to unregister the JMX name
	//原因：SpringでBasicDataSourceを二回目にクローズしようとした
	//1.BasicDataSource close itself automatically when application close
	//2.Spring use default destroy method to close DataSource but it's already closed
	//@Bean(destroyMethod = "close")
	@Bean(destroyMethod = "")					//バッチ 接続：１回のみ
	public DataSource dataSource() {
		//DBCP2のデータソースを使う
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:mysql://tonoyo-test-db.coeenstqrhsg.us-east-2.rds.amazonaws.com:3306/tonoyo?useSSL=false");
		dataSource.setUsername("longchamp");// 用户名
		dataSource.setPassword("longchamp");// 密码
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitialSize(3);// 初始化时建立物理连接的个数
		dataSource.setMaxTotal(10);// 最大连接池数量
		dataSource.setMinIdle(3);// 最小连接池数量
		dataSource.setTimeBetweenEvictionRunsMillis(60000);// 获取连接时最大等待时间，单位毫秒。
		dataSource.setValidationQuery("SELECT 1 FROM DUAL");// 用来检测连接是否有效的sql
		dataSource.setTestOnBorrow(true);// 申请连接时执行validationQuery检测连接是否有效
		dataSource.setTestWhileIdle(true);// 建议配置为true，不影响性能，并且保证安全性。
		dataSource.setPoolPreparedStatements(false);// 是否缓存preparedStatement，也就是PSCache
		return dataSource;
	}*/

	@Bean
	protected Tasklet tasklet() {

		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext context) {
				System.out.println("Tasklet: execute");
				return RepeatStatus.FINISHED;
			}
		};

	}

	@Bean
	public Job job() throws Exception {
		System.out.println("Job: build");
		return this.jobs.get("job").start(step1()).build();
	}

	@Bean
	protected Step step1() throws Exception {
		System.out.println("Step1: build");
		return this.steps.get("step1").tasklet(tasklet()).build();
	}

	public static void main(String[] args) throws Exception {
		// System.exit is common for Batch applications since the exit code can
		// be used to
		// drive a workflow
		System.exit(SpringApplication.exit(SpringApplication.run(TaskletBatchApplication.class, args)));
	}

}