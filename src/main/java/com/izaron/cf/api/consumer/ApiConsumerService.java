package com.izaron.cf.api.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.domain.Party;
import com.izaron.cf.domain.Submission;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ApiConsumerService implements ApiConsumer {

    private final ObjectMapper jacksonMapper;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ApiConsumerService(ObjectMapper jacksonMapper,
                              RestTemplate restTemplate,
                              @Value("${codeforces.api.base-url}") String baseUrl) {
        this.jacksonMapper = jacksonMapper;
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public String sendQuery(String method, MultiValueMap<String, String> params) {
        String path = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path(method)
                .queryParams(params)
                .build().toUriString();
        try {
            String result = restTemplate.getForObject(path, String.class);
//            if (method.equals("contest.status")) {
//                result = filterContestStatus(result);
//            }
            return result;
        } catch (HttpStatusCodeException e) {
            return "";
        } catch (Exception e) {
            return null;
        }
    }

    private String filterContestStatus(String result) {
        if (Objects.isNull(result)) {
            return null;
        }

        JsonNode node = null;
        try {
            node = jacksonMapper.readTree(result);
        } catch (IOException e) {
            return null;
        }

        if (!node.has("result")) {
            return null;
        }
        node = node.get("result");

        List<Submission> submissions = new ArrayList<>();
        int pos = 0;
        while (node.has(pos)) {
            try {
                Submission submission = jacksonMapper.readValue(node.get(pos).toString(), Submission.class);
                if (submission.getAuthor().getParticipantType() == Party.ParticipantType.CONTESTANT) {
                    submissions.add(submission);
                }
            } catch (IOException e) {
                return null;
            }
            pos++;
        }

        try {
            Response response = new Response();
            response.setResult(jacksonMapper.writeValueAsString(submissions));
            return jacksonMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Data
    private static class Response {
        private String result;
    }
}
