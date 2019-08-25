package com.izaron.cf.api.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.domain.Party;
import com.izaron.cf.domain.Submission;
import com.izaron.cf.mongo.types.ApiQuery;
import com.izaron.cf.mongo.repository.ApiQueryRepository;
import com.izaron.cf.service.CompressionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ApiConsumerMongoService implements ApiConsumer {

    private final ApiConsumer apiConsumer;
    private final ApiQueryRepository apiQueryRepository;
    private final CompressionService compressionService;
    private final ObjectMapper jacksonMapper;

    public ApiConsumerMongoService(@Qualifier("apiConsumerTimerService") ApiConsumer apiConsumer,
                                   ApiQueryRepository apiQueryRepository,
                                   CompressionService compressionService,
                                   ObjectMapper jacksonMapper) {
        this.apiConsumer = apiConsumer;
        this.apiQueryRepository = apiQueryRepository;
        this.compressionService = compressionService;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public String sendQuery(String method, MultiValueMap<String, String> params) {
        List<ApiQuery> prevQuery = apiQueryRepository.findByMethodNameAndParams(method, params);
        if (prevQuery.isEmpty()) {
            String result;

            if (method.equals("contest.status")) {
                List<Submission> submissions = new ArrayList<>();

                long from = 1L, step = 5000L;
                while (true) {
                    params.put("from", Collections.singletonList(String.valueOf(from)));
                    params.put("count", Collections.singletonList(String.valueOf(step)));
                    String currentResult = apiConsumer.sendQuery(method, params);
                    params.remove("from");
                    params.remove("count");
                    List<Submission> currentPart = getSubmissions(currentResult);
                    if (currentPart.isEmpty()) {
                        break;
                    } else {
                        for (Submission submission : currentPart) {
                            if (submission.getAuthor().getParticipantType() == Party.ParticipantType.CONTESTANT) {
                                submissions.add(submission);
                            }
                        }
                        from += step;
                    }
                }

                try {
                    Response response = new Response();
                    response.setResult(jacksonMapper.writeValueAsString(submissions));
                    result = jacksonMapper.writeValueAsString(response);
                } catch (JsonProcessingException e) {
                    return null;
                }
            } else {
                result = apiConsumer.sendQuery(method, params);
            }

            if (Objects.nonNull(result)) {
                apiQueryRepository.save(ApiQuery.of(method, params, compressionService.zip(result)));
            }
            return result;
        } else {
            return compressionService.unzip(prevQuery.get(0).getResult());
        }
    }

    private List<Submission> getSubmissions(String result) {
        if (Objects.isNull(result)) {
            return Collections.emptyList();
        }

        JsonNode node = null;
        try {
            node = jacksonMapper.readTree(result);
        } catch (IOException e) {
            return Collections.emptyList();
        }

        if (Objects.isNull(node) || !node.has("result")) {
            return Collections.emptyList();
        }
        node = node.get("result");

        List<Submission> submissions = new ArrayList<>();
        int pos = 0;
        while (node.has(pos)) {
            try {
                Submission submission = jacksonMapper.readValue(node.get(pos).toString(), Submission.class);
                submissions.add(submission);
            } catch (IOException e) {
                return Collections.emptyList();
            }
            pos++;
        }
        return submissions;
    }

    @Data
    private static class Response {
        private String result;
    }
}
