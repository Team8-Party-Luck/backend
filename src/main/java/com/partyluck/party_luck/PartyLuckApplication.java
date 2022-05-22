package com.partyluck.party_luck;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class PartyLuckApplication extends SpringBootServletInitializer {

    @PostConstruct
    public void started() {
        // timezone Asia/Seoul 셋팅
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.properties,"
            + "classpath:aws.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(PartyLuckApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}