package jp.co.drm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;

import jp.co.drm.base.integration.mybatis.entity.Person;
import jp.co.drm.batch.chunk.processor.SendMailItemProcessor;

//@SpringBootApplication(scanBasePackages={"com.sample.commons", "com.sample.product"})
@SpringBootApplication(scanBasePackages = { "jp.co.drm" }) // デフォルト：当クラスのpackage
@MapperScan("jp.co.drm.**.integration.mybatis.dao")
@EnableBatchProcessing
public class SendMailBatchApplication {



	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	private ItemReader<Person> sendMalItemReader;

	@Autowired
	private SendMailItemProcessor sendMailItemProcessor;

	@Autowired
	private ItemWriter<SimpleMailMessage> simpleEmailWriter;


	@Bean
	public Job jobSendMail() throws Exception {
		System.out.println("jobSendMail: build");
		return this.jobs.get("jobSendMail").start(stepSendMail()).build();
	}

	@Bean
	protected Step stepSendMail() throws Exception {
		System.out.println("stepSendMail: build");
		/*return steps.get("stepCsv2Db").<Person, Person>chunk(10).reader(csvItemReader()).processor(processor())
				.writer(myBatisItemReader()).build();*/
		// csvItemReaderメソッドを別のクラスに格納する場合
		return steps.get("stepSendMail").<Person, SimpleMailMessage>chunk(10).reader(sendMalItemReader).processor(sendMailItemProcessor)
				.writer(simpleEmailWriter).build();
	}


/*    @Bean
    public SendMailItemProcessor sendMailProcessor() {
        return new SendMailItemProcessor();
    }
*/




	public static void main(String[] args) throws Exception {
		// System.exit is common for Batch applications since the exit code can
		// be used to
		// drive a workflow
		System.exit(SpringApplication.exit(SpringApplication.run(SendMailBatchApplication.class, args)));
	}

}