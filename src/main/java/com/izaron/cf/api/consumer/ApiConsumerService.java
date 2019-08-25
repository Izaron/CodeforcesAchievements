package com.izaron.cf.api.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ApiConsumerService implements ApiConsumer {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ApiConsumerService(RestTemplate restTemplate,
                              @Value("${codeforces.api.base-url}") String baseUrl) {
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
            return restTemplate.getForObject(path, String.class);
        } catch (HttpStatusCodeException e) {
            return "";
        } catch (Exception e) {
            return null;
        }
    }
}
