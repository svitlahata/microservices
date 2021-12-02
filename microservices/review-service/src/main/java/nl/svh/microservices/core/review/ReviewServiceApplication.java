package nl.svh.microservices.core.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@ComponentScan(basePackages = "nl.svh.microservices")
public class ReviewServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceApplication.class);

    private final Integer threadPoolSize;
    private final Integer taskQueueSize;


    @Autowired
    public ReviewServiceApplication(
            @Value("${app.threadPoolSize:10}") Integer threadPoolSize,
            @Value("${app.taskQueueSize:100}") Integer taskQueueSize) {
        this.threadPoolSize = threadPoolSize;
        this.taskQueueSize = taskQueueSize;
    }

    @Bean
    public Scheduler jdbcScheduler(){
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "jdbc-pool");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReviewServiceApplication.class, args);
        String mysqlUrl = context.getEnvironment().getProperty("spring.datasource.url");
        LOGGER.info("Connect to Mysql : {}", mysqlUrl);
    }

}
