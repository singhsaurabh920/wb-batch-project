package org.worldbuild.batch.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.worldbuild.batch.document.domain.Person;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
@Profile("file-batch-operation")
public class FileBatchConfiguration {

    @Value("${spring.batch.file-path.input}")
    private String inputFilePath;
    @Value("${spring.batch.file-path.output}")
    private String outputFilePath;

    private final ResourceLoader resourceLoader;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @PostConstruct
    private void init(){
        log.info("Input file Path:"+inputFilePath);
        log.info("Output file Path:"+outputFilePath);
    }

    public FileBatchConfiguration(ResourceLoader resourceLoader,JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.resourceLoader=resourceLoader;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job fileJob(ItemReader<Person> batchFileReader, ItemProcessor<Person,Person> batchFileProcessor,ItemWriter<Person> batchFileWriter) {
        Step step1 = this.stepBuilderFactory.get("load-file")
                .<Person, Person>chunk(1000)
                .reader(batchFileReader)
                .processor(batchFileProcessor)
                .writer(batchFileWriter)
                .build();
        return this.jobBuilderFactory.get("file-job")
                .start(step1)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public ItemReader<Person> batchFileReader() {
        Resource resource = resourceLoader.getResource(inputFilePath);
        FlatFileItemReader flatFileItemReader=new FlatFileItemReader();
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setResource(resource);
        flatFileItemReader.setStrict(true);
        flatFileItemReader.setName("file-reader");
        flatFileItemReader.setLineMapper(lineMapper());
        return  flatFileItemReader;
    }

    @Bean
    public  LineMapper lineMapper() {
        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("name","age");
        //
        BeanWrapperFieldSetMapper fieldSetMapper=new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(Person.class);
        //
        DefaultLineMapper<Person> defaultLineMapper=new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
}
