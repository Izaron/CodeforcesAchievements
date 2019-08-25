package com.izaron.cf.api.consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class ApiConsumerTimerServiceTest {

    @Mock
    private ApiConsumer apiConsumer;

    @Test
    void sendManyQueries() {
        Mockito.when(apiConsumer.sendQuery(any(), any()))
                .thenReturn("Mock Value!");

        // The program without limitations can easily return more than just 7 values in 150ms
        // So we test that it won't have more than 7 returns in consecutive 500ms
        int delayMs = 500;
        int permits = 7;
        int callCount = 120;

        ApiConsumerTimerService service = new ApiConsumerTimerService(apiConsumer, delayMs, permits);
        List<Long> callAnswerMillis = new ArrayList<>();
        for (int i = 0; i < callCount; i++) {
            service.sendQuery("some.method", null);
            callAnswerMillis.add(System.currentTimeMillis());
        }

        for (int i = 0; i + permits < callAnswerMillis.size(); i++) {
            assertTrue(callAnswerMillis.get(i + permits) - callAnswerMillis.get(i) >= delayMs);
        }
    }
}