package jp.co.drm.batch.chunk.config;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.mail.SimpleMailMessageItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class SendMailWriterConfig {

	@Autowired
	private JavaMailSender mailSender;

	@Bean
	public ItemWriter<SimpleMailMessage> simpleEmailWriter() {
		SimpleMailMessageItemWriter writer = new SimpleMailMessageItemWriter();
		writer.setMailSender(mailSender);

		return writer;
	}

}
