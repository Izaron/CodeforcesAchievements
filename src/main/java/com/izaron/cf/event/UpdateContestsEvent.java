package com.izaron.cf.event;

import com.izaron.cf.domain.Contest;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Data
public class UpdateContestsEvent extends ApplicationEvent {

    private List<Contest> contests;

    public UpdateContestsEvent(Object source, List<Contest> contests) {
        super(source);
        this.contests = contests;
    }

    public static UpdateContestsEvent of(Object source, List<Contest> contests) {
        return new UpdateContestsEvent(source, contests);
    }
}
