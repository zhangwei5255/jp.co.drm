//package jp.co.drm;
//
//import java.io.File;
//
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//
//import jp.co.drm.batch.chunk.processor.ImageItemProcessor;
//import jp.co.drm.batch.item.dto.ImageDto;
//import jp.co.drm.batch.partition.ImagePartitioner;
//import jp.co.drm.listener.job.UploadImageJobExecutionListener;
//
////@SpringBootApplication(scanBasePackages={"com.sample.commons", "com.sample.product"})
//@SpringBootApplication(scanBasePackages = { "jp.co.drm" }) // デフォルト：当クラスのpackage
//@MapperScan("jp.co.drm.**.integration.mybatis.dao")
//@EnableBatchProcessing
//public class PartitionUploadImageBatchApplication {
//
//	@Autowired
//	private JobBuilderFactory jobs;
//
//	@Autowired
//	private StepBuilderFactory steps;
//
//	@Autowired
//	SimpleAsyncTaskExecutor taskExecutor;
//
//	@Autowired
//	private ItemReader<File> partitionImageFileItemReader;
//
//	@Autowired
//	private ImageItemProcessor imageItemProcessor;
//
//	@Autowired
//	private ImagePartitioner imagePartitioner;
//
//	@Autowired
//	private ItemWriter<ImageDto> imageFileItemWriter;
//
//
//
//	@Bean
//	public Job jobPartitionUploadImage() throws Exception {
//		System.out.println("jobPartitionUploadImage: build");
//		return this.jobs.get("jobPartitionUploadImage").start(stepMasterUploadImage()).listener(new UploadImageJobExecutionListener()) .build();
//	}
//
//	@Bean
//	protected Step stepMasterUploadImage() throws Exception {
//		System.out.println("stepMasterUploadImage: build");
//		/*return steps.get("stepCsv2Db").<Person, Person>chunk(10).reader(csvItemReader()).processor(processor())
//				.writer(myBatisItemReader()).build();*/
//		// csvItemReaderメソッドを別のクラスに格納する場合
//		/*TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
//		handler.setStep(stepSlaveUploadImage());
//		handler.setGridSize(10);
//		handler.setTaskExecutor(taskExecutor);*/
//
//		//return steps.get("stepMasterUploadImage").partitioner("stepSlaveUploadImage", imagePartitioner).gridSize(10).partitionHandler(handler).build();
//		//return steps.get("stepMasterUploadImage").partitioner("stepSlaveUploadImage", imagePartitioner).gridSize(10).taskExecutor(taskExecutor).build();
//		 //steps.get("stepMasterUploadImage").partitioner(stepSlaveUploadImage()).gridSize(10).taskExecutor(taskExecutor).build();
//
//		return steps.get("stepMasterUploadImage").partitioner(stepSlaveUploadImage()).partitioner("stepSlaveUploadImage", imagePartitioner)
//				.gridSize(10).taskExecutor(taskExecutor).build();
//
//
//	/*	return steps.get("stepUploadImage").<File, ImageDto>chunk(10).reader(imageFileItemReader).processor(imageItemProcessor)
//				.build();*/
//	}
//
//	@Bean
//	protected Step stepSlaveUploadImage() throws Exception {
//		System.out.println("stepSlaveUploadImage: build");
//		/*return steps.get("stepCsv2Db").<Person, Person>chunk(10).reader(csvItemReader()).processor(processor())
//				.writer(myBatisItemReader()).build();*/
//		// csvItemReaderメソッドを別のクラスに格納する場合
//		return steps.get("stepSlaveUploadImage").<File, ImageDto>chunk(10).reader(partitionImageFileItemReader).processor(imageItemProcessor)
//				.writer(imageFileItemWriter).build();
//
//	}
//
//
//
//
//
//
//	public static void main(String[] args) throws Exception {
//		// System.exit is common for Batch applications since the exit code can
//		// be used to
//		// drive a workflow
//		System.exit(SpringApplication.exit(SpringApplication.run(PartitionUploadImageBatchApplication.class, args)));
//		//SpringApplication.run(PartitionUploadImageBatchApplication.class, args);
//	}
//
//}