package jp.co.drm.batch.chunk.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.co.drm.base.integration.mybatis.dao.PersonDao;
import jp.co.drm.base.integration.mybatis.entity.Person;

@Configuration
public class SendMailReaderConfig {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Bean
	public ItemReader<Person> sendMalItemReader() {
		MyBatisPagingItemReader<Person> reader = new MyBatisPagingItemReader<Person>();
		reader.setSqlSessionFactory(sqlSessionFactory);
		// reader.setParameterValues(parameterValues);
		//PersonDao.class.getName()

		//writer.setStatementId("jp.co.drm.base.integration.mybatis.dao.PersonDao.insert");
		reader.setQueryId(PersonDao.class.getName() + ".selectAll");
		System.out.println("sendMalItemReader start");

		return reader;
	}


}
