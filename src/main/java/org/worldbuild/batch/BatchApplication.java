package org.worldbuild.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication(exclude={
		DataSourceAutoConfiguration.class,
		/*ErrorMvcAutoConfiguration.class*/
		/*MongoDataAutoConfiguration.class*/
})
public class BatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

}
