package org.worldbuild.batch.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.worldbuild.batch.document.domain.Person;

import java.util.List;

@Log4j2
@Component("batchFileWriter")
public class BatchFileWriter implements ItemWriter<Person> {

    @Override
    public void write(List<? extends Person> list) throws Exception {
        log.info("Writing Person:"+ list);
    }
}
