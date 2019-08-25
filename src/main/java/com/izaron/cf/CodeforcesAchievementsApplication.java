package com.izaron.cf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.Contest;
import com.izaron.cf.domain.Submission;
import com.izaron.cf.domain.params.ContestStatusParams;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootApplication
@Log4j2
public class CodeforcesAchievementsApplication {

	@Autowired
	private ApiMethods apiMethods;

	public static void main(String[] args) {
		SpringApplication.run(CodeforcesAchievementsApplication.class, args);
	}

//	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		Set<String> languages = new TreeSet<>();
		List<Contest> contests = apiMethods.getContestList();
		contests.sort(Comparator.comparing(Contest::getId));
		for (Contest contest : contests) {
			long contestId = contest.getId();
			log.debug("Working with contest {}", contestId);

			ContestStatusParams params = ContestStatusParams.builder()
					.contestId(contestId)
					.build();

			List<Submission> submissions = apiMethods.getContestStatus(params);
			if (Objects.isNull(submissions)) {
				log.debug("The contest {} has no status!", contestId);
			} else {
				for (Submission submission : submissions) {
					String language = submission.getProgrammingLanguage();
					if (languages.add(language)) {
						log.debug("Got new language: {} [CONTEST {}]", language, contestId);
					}
				}
			}
		}
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
