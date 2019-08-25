package com.izaron.cf.domain.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatusParams {

    String handle;
    Long from;
    Long count;
}
