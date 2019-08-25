package com.izaron.cf.api.consumer;

import org.springframework.util.MultiValueMap;

public interface ApiConsumer {

    public String sendQuery(String method, MultiValueMap<String, String> params);
}
