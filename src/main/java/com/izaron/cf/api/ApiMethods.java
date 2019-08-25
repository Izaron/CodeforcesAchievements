package com.izaron.cf.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.api.consumer.ApiConsumer;
import com.izaron.cf.domain.*;
import com.izaron.cf.domain.extra.ContestStandings;
import com.izaron.cf.domain.extra.ProblemsetProblems;
import com.izaron.cf.domain.params.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiMethods {

    private final ApiConsumer apiConsumer;
    private final ObjectMapper jacksonMapper;

    @Value("${codeforces.api.contest-status-count}")
    private int contestStatusCount;

    public ApiMethods(@Qualifier("apiConsumerMongoService") ApiConsumer apiConsumer,
                      ObjectMapper jacksonMapper) {
        this.apiConsumer = apiConsumer;
        this.jacksonMapper = jacksonMapper;
    }

    public List<Comment> getBlogEntryComments(long blogEntryId) {
        return parseList("blogEntry.comments", singleParam("blogEntryId", blogEntryId),
                Comment.class);
    }

    public BlogEntry getBlogEntryView(long blogEntryId) {
        return parse("blogEntry.view", singleParam("blogEntryId", blogEntryId),
            BlogEntry.class);
    }

    public List<Hack> getContestHacks(long contestId) {
        return parseList("contest.hacks", singleParam("contestId", contestId),
                Hack.class);
    }

    public List<Contest> getContestList() {
        return getContestList(false);
    }

    public List<Contest> getContestList(boolean gym) {
        return parseList("contest.list", singleParam("gym", gym),
                Contest.class);
    }

    public List<RatingChange> getContestRatingChanges(long contestId) {
        return parseList("contest.ratingChanges", singleParam("contestId", contestId),
                RatingChange.class);
    }

    public ContestStandings getContestStandings(ContestStandingsParams params) {
        return parse("contest.standings", pojoParam(params), ContestStandings.class);
    }

    public List<Submission> getContestStatus(ContestStatusParams params) {
        return parseList("contest.status", pojoParam(params), Submission.class);
    }

    public ProblemsetProblems getProblemsetProblems(ProblemsetProblemsParams params) {
        return parse("problemset.problems", pojoParam(params), ProblemsetProblems.class);
    }

    public List<Submission> getProblemsetRecentStatus(ProblemsetRecentStatusParams params) {
        return parseList("problemset.recentStatus", pojoParam(params), Submission.class);
    }

    public List<RecentAction> getRecentActions(long maxCount) {
        return parseList("recentActions", singleParam("maxCount", maxCount), RecentAction.class);
    }

    public List<BlogEntry> getUserBlogEntries(String handle) {
        return parseList("user.blogEntries", singleParam("handle", handle), BlogEntry.class);
    }

    public List<User> getUserInfo(List<String> handles) {
        return parseList("user.info", singleParamList("handles", handles), User.class);
    }

    public List<User> getUserRatedList() {
        return getUserRatedList(false);
    }

    public List<User> getUserRatedList(boolean activeOnly) {
        return parseList("user.ratedList", singleParam("activeOnly", activeOnly), User.class);
    }

    public List<RatingChange> getUserRating(String handle) {
        return parseList("user.rating", singleParam("handle", handle), RatingChange.class);
    }

    public List<Submission> getUserStatus(UserStatusParams params) {
        return parseList("user.status", pojoParam(params), Submission.class);
    }

    private <T> T parse(String method, MultiValueMap<String, String> params, Class<T> type) {
        String call = apiConsumer.sendQuery(method, params);
        try {
            JsonNode node = jacksonMapper.readTree(call);
            if (!node.has("result")) {
                return null;
            }
            return jacksonMapper.readValue(node.get("result").toString(), type);
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    private <T> List<T> parseList(String method, MultiValueMap<String, String> params, Class<T> type) {
        String call = apiConsumer.sendQuery(method, params);
        try {
            JsonNode node = jacksonMapper.readTree(call);
            if (!node.has("result")) {
                return Collections.emptyList();
            }
            node = node.get("result");
            if (node.isTextual()) {
                node = jacksonMapper.readTree(node.asText());
            }

            int pos = 0;
            List<T> list = new ArrayList<>();
            while (node.has(pos)) {
                list.add(jacksonMapper.readValue(node.get(pos).toString(), type));
                pos++;
            }
            return list;
        } catch (IOException | NullPointerException e) {
            return Collections.emptyList();
        }
    }

    private <T> MultiValueMap<String, String> singleParam(String paramKey, T paramValue) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(paramKey, paramValue.toString());
        return params;
    }

    private <T> MultiValueMap<String, String> singleParamList(String paramKey, List<T> paramValue) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        List<String> list = paramValue.stream().map(Object::toString).collect(Collectors.toList());
        params.put(paramKey, list);
        return params;
    }

    private <T> MultiValueMap<String, String> pojoParam(T pojoObject) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        try {
            String json = jacksonMapper.writeValueAsString(pojoObject);
            Map<String, Object> map = jacksonMapper.readValue(json, Map.class);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    if (entry.getValue() instanceof List) {
                        for (Object elem : ((List) value)) {
                            params.add(entry.getKey(), elem.toString());
                        }
                    } else {
                        params.add(entry.getKey(), value.toString());
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            return new LinkedMultiValueMap<>();
        }
        return params;
    }
}
