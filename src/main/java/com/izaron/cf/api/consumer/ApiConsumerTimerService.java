package com.izaron.cf.api.consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

@Service
public class ApiConsumerTimerService implements ApiConsumer {

    private final ApiConsumer apiConsumer;
    private final Integer delay;

    private Semaphore semaphore;
    private Timer timer;

    ApiConsumerTimerService(@Qualifier("apiConsumerService") ApiConsumer apiConsumer,
                            @Value("${codeforces.api.delay}") Integer delay,
                            @Value("${codeforces.api.permits}") Integer semaphorePermits) {
        this.apiConsumer = apiConsumer;
        this.delay = delay;
        this.semaphore = new Semaphore(semaphorePermits, true);
        this.timer = new Timer();
    }

    @Override
    public String sendQuery(String method, MultiValueMap<String, String> params) {
        try {
            semaphore.acquire();
            String result = apiConsumer.sendQuery(method, params);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    semaphore.release();
                }
            }, delay);
            return result;
        } catch (InterruptedException e) {
            return StringUtils.EMPTY;
        }
    }
}
