package jp.co.drm;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import jp.co.drm.base.integration.mybatis.dao.PersonDao;
import jp.co.drm.base.integration.mybatis.entity.Person;
import jp.co.drm.batch.chunk.processor.PersonItemProcessor;

//@SpringBootApplication(scanBasePackages={"com.sample.commons", "com.sample.product"})
@SpringBootApplication(scanBasePackages = { "jp.co.drm" }) // デフォルト：当クラスのpackage
@MapperScan("jp.co.drm.**.integration.mybatis.dao")
@EnableBatchProcessing
public class Csv2DBBatchApplication {

	 private final static String FILE_NAME = "sample-data.csv";

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private ItemReader<Person> csvItemReader2;

	@Bean
	public Job jobCsv2Db() throws Exception {
		System.out.println("jobCsv2Db: build");
		return this.jobs.get("jobCsv2Db").start(stepCsv2Db()).build();
	}

	@Bean
	protected Step stepCsv2Db() throws Exception {
		System.out.println("stepCsv2Db: build");
		/*return steps.get("stepCsv2Db").<Person, Person>chunk(10).reader(csvItemReader()).processor(processor())
				.writer(myBatisItemReader()).build();*/
		// csvItemReaderメソッドを別のクラスに格納する場合
		return steps.get("stepCsv2Db").<Person, Person>chunk(10).reader(csvItemReader2).processor(processor())
				.writer(myBatisItemWriter()).build();
	}

	@Bean
	// public ItemReader<Person> csvItemReader(String fileName) {
	public ItemReader<Person> csvItemReader() {
		FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource(FILE_NAME));
		// reader.setResource(new ClassPathResource(fileName));
		// reader.setResource(new FileSystemResource(FILE_NAME)); // read system
		// file.
		// reader.setLinesToSkip(1); // skip header.
		reader.setLineMapper(new DefaultLineMapper<Person>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						// setDelimiter(DelimitedLineTokenizer.DELIMITER_TAB);
						// // read tvs.
						// setNames(new String[]{"firstName", "lastName",
						// "mail"});
						setNames(new String[] { "firstName", "lastName" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				});
			}
		});



		return reader;
	}


    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }


	@Bean
	public ItemWriter<Person> myBatisItemWriter() {
		MyBatisBatchItemWriter<Person> writer = new MyBatisBatchItemWriter<Person>();
		writer.setSqlSessionFactory(sqlSessionFactory);
		// reader.setParameterValues(parameterValues);
		//PersonDao.class.getName()

		//writer.setStatementId("jp.co.drm.base.integration.mybatis.dao.PersonDao.insert");
		writer.setStatementId(PersonDao.class.getName() + ".insert");

		return writer;
	}


	public static void main(String[] args) throws Exception {
		// System.exit is common for Batch applications since the exit code can
		// be used to
		// drive a workflow
		System.exit(SpringApplication.exit(SpringApplication.run(Csv2DBBatchApplication.class, args)));
	}

}