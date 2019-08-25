package com.izaron.cf.api.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.mongo.types.ApiQuery;
import com.izaron.cf.mongo.repository.ApiQueryRepository;
import com.izaron.cf.service.CompressionService;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringRunner.class)
@SpringBootTest
class ApiConsumerMongoServiceTest {

    @Autowired
    private ApiQueryRepository apiQueryRepository;

    @Mock
    private ApiConsumer apiConsumer;

    @Test
    void sendQueryTest() throws IOException {
        String methodName = "blogEntry.comments";
        String paramName = "blogEntryId";
        String paramValue = "79";

        // Mock Codeforces API
        String blogEntryComments = IOUtils.toString(
            this.getClass().getResourceAsStream("/mock/blogEntryComments.json"),
            StandardCharsets.UTF_8
        );
        Mockito.when(apiConsumer.sendQuery(
                eq(methodName),
                argThat(arg -> Objects.equals(arg.getFirst(paramName), paramValue))))
                .thenReturn(blogEntryComments);

        // Use database layer service
        ApiConsumerMongoService service = new ApiConsumerMongoService(apiConsumer, apiQueryRepository,
                new CompressionService(), new ObjectMapper());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(paramName, paramValue);
        String result = service.sendQuery(methodName, params);

        // Check row in database
        List<ApiQuery> queries = apiQueryRepository.findByMethodNameAndParams(methodName, params);
        Assert.assertNotNull(queries);
        Assert.assertEquals(1, queries.size());

        ApiQuery query = queries.get(0);
        Assert.assertEquals(methodName, query.getMethodName());
        Assert.assertEquals(params, query.getParams());
    }
}