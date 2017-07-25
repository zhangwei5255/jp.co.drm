package jp.co.drm.batch.chunk.config;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.co.drm.batch.item.dto.ImageDto;
import jp.co.drm.batch.item.file.ImageFileItemWriter;

@Configuration
public class UploadImageWriterConfig {

	/*@Autowired
	ImageFileItemWriter imageFileItemWriter;*/

	@Bean
	public ItemWriter<ImageDto> imageFileItemWriter() throws Exception {
		ImageFileItemWriter writer = new ImageFileItemWriter();
		return writer;
	}

}
