package jp.co.drm.batch.chunk.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jp.co.drm.base.integration.mybatis.entity.Person;


@Component
public class SendMailItemProcessor implements ItemProcessor<Person, SimpleMailMessage> {
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mail.fromMail.addr}")
    private String from;

    @Override
    public SimpleMailMessage process(Person item) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();

     //   log.debug("{} {} email:{}", item.getFirstName(), item.getLastName(), item.getMail());

        message.setFrom(from);
        message.setTo("test@153.com");
        message.setSubject("Welcome!");

        //创建邮件正文
        Context context = new Context();
        context.setVariable("id", "006");
        String body = templateEngine.process("emailTemplate", context);

        message.setText(body);

        return message;
    }
}
