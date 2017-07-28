package jp.co.drm;

import java.io.File;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jp.co.drm.batch.chunk.processor.ImageItemProcessor;
import jp.co.drm.batch.item.dto.ImageDto;
import jp.co.drm.listener.job.UploadImageJobExecutionListener;

//@SpringBootApplication(scanBasePackages={"com.sample.commons", "com.sample.product"})
@SpringBootApplication(scanBasePackages = { "jp.co.drm" }) // デフォルト：当クラスのpackage
@MapperScan("jp.co.drm.**.integration.mybatis.dao")
//@EnableBatchProcessing
public class UploadImageBatchApplication {



	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	private ItemReader<File> imageFileItemReader;

	@Autowired
	private ImageItemProcessor imageItemProcessor;



	@Autowired
	private ItemWriter<ImageDto> imageFileItemWriter;


	@Bean
	public Job jobUploadImage() throws Exception {
		System.out.println("jobUploadImage: build");
		return this.jobs.get("jobUploadImage").start(stepUploadImage()).listener(new UploadImageJobExecutionListener()) .build();
	}

	@Bean
	protected Step stepUploadImage() throws Exception {
		System.out.println("stepUploadImage: build");
		/*return steps.get("stepCsv2Db").<Person, Person>chunk(10).reader(csvItemReader()).processor(processor())
				.writer(myBatisItemReader()).build();*/
		// csvItemReaderメソッドを別のクラスに格納する場合
		return steps.get("stepUploadImage").<File, ImageDto>chunk(10).reader(imageFileItemReader).processor(imageItemProcessor)
				.writer(imageFileItemWriter).build();


	/*	return steps.get("stepUploadImage").<File, ImageDto>chunk(10).reader(imageFileItemReader).processor(imageItemProcessor)
				.build();*/
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
		System.exit(SpringApplication.exit(SpringApplication.run(UploadImageBatchApplication.class, args)));
	}

}