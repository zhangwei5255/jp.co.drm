package jp.co.drm.batch.chunk.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import jp.co.drm.base.integration.mybatis.entity.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Autowired
    private Validator validator;

    @Override
    public Person process(final Person person) throws Exception {

    	//エラーチェック
    	  DataBinder binder = new DataBinder(person);
          binder.setValidator(validator);
          binder.validate();
          BindingResult result = binder.getBindingResult();

          if (result.hasErrors()) {
              log.warn(person.toString());
              result.getAllErrors().forEach(s -> log.warn(s.toString()));
              return null; // ItemProcessorでnullを返すと後続のWriterで処理されない
          }

         String firstName = person.getFirstName().toUpperCase();
         String lastName = person.getLastName().toUpperCase();

         Person transformedPerson = new Person();
        transformedPerson.setFirstName(firstName);
        transformedPerson.setLastName(lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
