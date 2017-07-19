package jp.co.drm.batch.config.chunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import jp.co.drm.base.integration.mybatis.entity.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(final Person person) throws Exception {
         String firstName = person.getFirstName().toUpperCase();
         String lastName = person.getLastName().toUpperCase();

         Person transformedPerson = new Person();
        transformedPerson.setFirstName(firstName);
        transformedPerson.setLastName(lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
