package com.izaron.cf.mongo.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "api_query")
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class ApiQuery {

    @Id String id;
    @NonNull String methodName;
    @NonNull Map<String, List<String>> params;
    @NonNull byte[] result;
}
