package org.worldbuild.batch.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.worldbuild.batch.document.domain.Person;

@Log4j2
@Component("batchFileProcessor")
public class BatchFileProcessor implements ItemProcessor<Person,Person> {

    @Override
    public Person process(Person person) throws Exception {
        log.info("Person is processing:"+ person);
        return person;
    }
}
