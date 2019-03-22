package com.dane.brown;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unit test for simple App.
 */
@SpringBootApplication
@EnableScheduling
@RestController
public class AppTest extends SpringBootServletInitializer
{
    private Logger log = LogManager.getLogger(getClass());
    public static void main(String[] args) {
        SpringApplication.run(AppTest.class, args);
    }
    @RequestMapping("/")
    public String test(){
        test1();
        return "ok";
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AppTest.class);
    }
    @Scheduled(fixedDelay = 1000)
    public void test1(){
        System.out.println("test1");
        log.debug("test1");
    }
    @Scheduled(fixedRate = 1000)
    public void test2(){
        System.out.println("test2");
        log.debug("test2");
    }
    @Scheduled(cron = "* * * * * ?")
    public void test3(){
        System.out.println("test3");
        log.debug("test3");
    }

}
