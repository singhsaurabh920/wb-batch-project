package org.worldbuild.batch.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.worldbuild.batch.document.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Log4j2
@Configuration
@Profile("mongo-batch-operation")
public class MongoBatchConfiguration {

	private final MongoTemplate mongoTemplate;

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	public MongoBatchConfiguration(MongoTemplate mongoTemplate, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.mongoTemplate=mongoTemplate;
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	public Job mongoJob() {
		return this.jobBuilderFactory.get("job")
				.start(mongoJobStep1())
				.next(mongoJobStep2())
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Step mongoJobStep1() {
		return this.stepBuilderFactory.get("step1")
				.<Person, Person>chunk(1000)
				.reader(listItemReader())
				.writer(mongoItemWriter())
				.build();
	}

	@Bean
	public ListItemReader<Person> listItemReader() {
		List<Person> items = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			items.add(new Person(i+"","foo" + i));
		}
		return new ListItemReader<>(items);
	}

	@Bean
	public MongoItemReader<Person> mongoItemReader() {
		MongoItemReader reader=new MongoItemReader();
		reader.setCollection("person");
		reader.setTemplate(mongoTemplate);
		reader.setQuery(Query.query(Criteria.where("name").exists(true)));
		return reader;
	}

	@Bean
	public MongoItemWriter<Person> mongoItemWriter() {
		MongoItemWriter<Person> writer = new MongoItemWriter<>();
		writer.setCollection("person");
		writer.setTemplate(mongoTemplate);
		return writer;
	}

	@Bean
	public Step mongoJobStep2() {
		return this.stepBuilderFactory.get("step2")
				.tasklet((stepContribution, chunkContext) ->  {
					long nbPersonsPersisted = mongoTemplate.count(new Query(),Person.class);
					log.info(String.format("%s persons have been persisted", nbPersonsPersisted));
					return RepeatStatus.FINISHED;
				})
				.build();
	}

}
