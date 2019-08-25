package com.izaron.cf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.api.ApiMethods;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Log4j2
public class CodeforcesAchievementsApplication {

	@Autowired
	private ApiMethods apiMethods;

	public static void main(String[] args) {
		SpringApplication.run(CodeforcesAchievementsApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	ObjectMapper jacksonMapper() {
		return new ObjectMapper();
	}
}
