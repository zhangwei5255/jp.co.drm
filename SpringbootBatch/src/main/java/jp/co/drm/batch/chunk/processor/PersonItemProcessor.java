package jp.co.drm.batch.chunk.processor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import jp.co.drm.base.integration.mybatis.entity.Person;
import jp.co.drm.batch.step.skip.StepSkipException;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private Validator validator;

	@Override
	public Person process(final Person person) throws Exception {

		// エラーチェック
		DataBinder binder = new DataBinder(person);
		binder.setValidator(validator);
		binder.validate();
		BindingResult result = binder.getBindingResult();

		if (result.hasErrors()) {
			logger.warn(person.toString());
			result.getAllErrors().forEach(s -> logger.warn(s.toString()));
			List<String> errMsgs = result.getAllErrors().stream().map(p -> p.toString()).collect(Collectors.toList());

			// 処理方法１ return null
			// return null; // ItemProcessorでnullを返すとエラーデータのみが後続のWriterで処理されない
			// 処理方法２ throw Exception データが全てWriterで処理されない
			throw new StepSkipException(errMsgs);
		}

		String firstName = person.getFirstName().toUpperCase();
		String lastName = person.getLastName().toUpperCase();

		Person transformedPerson = new Person();
		transformedPerson.setFirstName(firstName);
		transformedPerson.setLastName(lastName);

		logger.info("Converting (" + person + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}
}
