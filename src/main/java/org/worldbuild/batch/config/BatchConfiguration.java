package org.worldbuild.batch.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.worldbuild.batch.document.Person;

import java.util.ArrayList;
import java.util.List;
@Log4j2
@Configuration
public class BatchConfiguration {

	private final MongoTemplate mongoTemplate;

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	public BatchConfiguration(MongoTemplate mongoTemplate,JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.mongoTemplate=mongoTemplate;
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
				.start(step1())
				.next(step2())
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
				.<Person, Person>chunk(1000)
				.reader(itemReader())
				.writer(itemWriter())
				.build();
	}

	@Bean
	public ListItemReader<Person> itemReader() {
		List<Person> items = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			items.add(new Person("foo" + i));
		}
		return new ListItemReader<>(items);
	}

	@Bean
	public MongoItemWriter<Person> itemWriter() {
		MongoItemWriter<Person> writer = new MongoItemWriter<>();
		writer.setTemplate(mongoTemplate);
		return writer;
	}

	@Bean
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
				.tasklet((stepContribution, chunkContext) ->  {
					long nbPersonsPersisted = mongoTemplate.count(new Query(),Person.class);
					log.info(String.format("%s persons have been persisted", nbPersonsPersisted));
					return RepeatStatus.FINISHED;
				})
				.build();
	}

}
