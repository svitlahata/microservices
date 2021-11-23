package nl.svh.microservices.core.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "nl.svh.microservices")
public class ReviewServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReviewServiceApplication.class, args);
        String mysqlUrl = context.getEnvironment().getProperty("spring.datasource.url");
        LOGGER.info("Connect to Mysql : {}", mysqlUrl);
    }

}
