package org.worldbuild.batch.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.TimeUnit;

@Log4j2
@Configuration
@Profile("schedule-script")
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler =new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setThreadNamePrefix("Job-scheduler-thread");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.scheduleWithFixedDelay(()->{
            log.info("Job Started...........");
            try {
                log.info("Job running...........");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("Job Stopped...........");
        },2000L);
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
