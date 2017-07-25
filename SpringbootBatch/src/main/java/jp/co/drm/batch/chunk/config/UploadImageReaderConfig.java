package jp.co.drm.batch.chunk.config;

import java.io.File;
import java.util.Locale;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.co.drm.batch.item.file.ImageFileItemReader;

@Configuration
public class UploadImageReaderConfig {

	@Autowired
	MessageSource messageSource;

	@Bean
	public ItemReader<File> imageFileItemReader() {
		String imageHome = messageSource.getMessage("image_home", null, Locale.getDefault());
		ImageFileItemReader reader = new ImageFileItemReader(imageHome);
		return reader;
	}

}
