package jp.co.drm.batch.chunk.config;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jp.co.drm.base.integration.mybatis.entity.Person;

@Configuration
public class CsvReaderConfig {

	 private final static String FILE_NAME = "sample-data.csv";

	@Bean
	// public ItemReader<Person> csvItemReader(String fileName) {
	public ItemReader<Person> csvItemReader2() {
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

}
