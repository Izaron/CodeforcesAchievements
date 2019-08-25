package com.izaron.cf.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaron.cf.api.consumer.ApiConsumer;
import com.izaron.cf.domain.*;
import com.izaron.cf.domain.extra.ContestStandings;
import com.izaron.cf.domain.extra.ProblemsetProblems;
import com.izaron.cf.domain.params.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ApiMethodsTest {

    @Autowired
    private ObjectMapper jacksonMapper;

    @Mock
    private ApiConsumer apiConsumer;

    @Test
    void getBlogEntryComments() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                    eq("blogEntry.comments"),
                    argThat(arg -> Objects.equals(arg.getFirst("blogEntryId"), "79"))))
                .thenReturn(loadFile("/mock/blogEntryComments.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert results
        List<Comment> comments = methods.getBlogEntryComments(79L);
        assertNotNull(comments);

        assertEquals(220L, comments.size());
        Comment randomComment = comments.get(29);
        assertEquals("AlexDmitriev", randomComment.getCommentatorHandle());
        assertEquals(33330L, (long) randomComment.getId());
        assertEquals(1303032406L, (long) randomComment.getCreationTimeSeconds());
        assertEquals("<div class=\"ttypography\">AFAIK,%lld is available with MS VC++." +
                        "<div>It isn't  available with g++, because it is MinGW g++,which not support" +
                        " this identificator</div></div>",
                randomComment.getText());
        assertEquals(32637L, (long) randomComment.getParentCommentId());
    }

    @Test
    void getBlogEntryView() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                    eq("blogEntry.view"),
                    argThat(arg -> Objects.equals(arg.getFirst("blogEntryId"), "79"))))
                .thenReturn(loadFile("/mock/blogEntryView.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert results
        BlogEntry entry = methods.getBlogEntryView(79L);
        assertNotNull(entry);

        assertEquals("MikeMirzayanov", entry.getAuthorHandle());
        assertEquals(79L, (long) entry.getId());
        assertEquals("<p>About the programming languages</p>", entry.getTitle());
        assertEquals(0L, (long) entry.getRating());
    }

    @Test
    void getContestHacks() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                    eq("contest.hacks"),
                    argThat(arg -> Objects.equals(arg.getFirst("contestId"), "566"))))
                .thenReturn(loadFile("/mock/contestHacks.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<Hack> hacks = methods.getContestHacks(566L);
        assertNotNull(hacks);

        assertEquals(325, hacks.size());

        Hack oneHack = hacks.get(0);
        assertEquals(1438274514L, (long) oneHack.getCreationTimeSeconds());
        assertEquals(160426L, (long) oneHack.getId());
        assertEquals("Sehnsucht", oneHack.getHacker().getMembers().get(0).getHandle());
        assertEquals(29L, (long) oneHack.getHacker().getRoom());
        assertEquals(Hack.Verdict.INVALID_INPUT, oneHack.getVerdict());

        Hack otherHack = hacks.get(62);
        assertEquals(1438278563L, (long) otherHack.getCreationTimeSeconds());
        assertEquals(160488L, (long) otherHack.getId());
        assertEquals("0ptimiZer", otherHack.getDefender().getMembers().get(0).getHandle());
        assertEquals(1L, (long) otherHack.getHacker().getRoom());
        assertEquals(Hack.Verdict.HACK_SUCCESSFUL, otherHack.getVerdict());
    }

    @Test
    void getContestList() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(eq("contest.list"), any()))
                .thenReturn(loadFile("/mock/contestList.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<Contest> contestList = methods.getContestList();
        assertNotNull(contestList);
        assertEquals(1144, contestList.size());

        Contest contest = contestList.get(13);
        assertEquals(Contest.Type.CF, contest.getType());
        assertEquals(Contest.Phase.FINISHED, contest.getPhase());
        assertEquals(false, contest.getFrozen());
        assertEquals(7200L, (long) contest.getDurationSeconds());
    }

    @Test
    void getContestRatingChanges() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                    eq("contest.ratingChanges"),
                    argThat(arg -> Objects.equals(arg.getFirst("contestId"), "566"))))
                .thenReturn(loadFile("/mock/contestRatingChanges.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<RatingChange> ratingChanges = methods.getContestRatingChanges(566L);
        assertNotNull(ratingChanges);
        assertEquals(761, ratingChanges.size());

        RatingChange change = ratingChanges.get(653);
        assertEquals("VK Cup 2015 - Finals, online mirror", change.getContestName());
        assertEquals("Sehnsucht", change.getHandle());
        assertEquals(425, (long) change.getRank());
        assertEquals(1697, (long) change.getOldRating());
        assertEquals(1633, (long) change.getNewRating());
    }

    @Test
    void getContestStandings() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(eq("contest.standings"), any()))
                .thenReturn(loadFile("/mock/contestStandings.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        ContestStandingsParams params = ContestStandingsParams.builder()
                .contestId(566L)
                .from(1L)
                .count(5L)
                .showUnofficial(true)
                .build();

        ContestStandings standings = methods.getContestStandings(params);
        assertNotNull(standings);

        assertEquals(566L, (long) standings.getContest().getId());
        assertEquals(7, standings.getProblems().size());
        assertEquals(5, standings.getRows().size());
        assertEquals("rng_58", standings.getRows().get(0).getParty().getMembers().get(0).getHandle());
    }

    @Test
    void getContestStatus() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(eq("contest.status"), any()))
                .thenReturn(loadFile("/mock/contestStatus.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        ContestStatusParams params = ContestStatusParams.builder()
                .contestId(566L)
                .from(1L)
                .count(10L)
                .build();

        List<Submission> status = methods.getContestStatus(params);
        assertNotNull(status);

        assertEquals(10, status.size());
        assertEquals(566L, (long) status.get(0).getContestId());
        assertEquals(58533313L, (long) status.get(0).getId());
        assertEquals("GNU C++14", status.get(0).getProgrammingLanguage());
        assertEquals(38L, (long) status.get(0).getPassedTestCount());
        assertEquals(Submission.Verdict.OK, status.get(0).getVerdict());
    }

    @Test
    void getProblemsetProblems() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("problemset.problems"),
                argThat(arg -> Objects.equals(arg.getFirst("tags"), "implementation"))))
                .thenReturn(loadFile("/mock/problemsetProblems.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        ProblemsetProblemsParams params = ProblemsetProblemsParams.builder()
                .tags(Collections.singletonList("implementation"))
                .build();

        ProblemsetProblems problemsetProblems = methods.getProblemsetProblems(params);
        assertNotNull(problemsetProblems);

        int problemCount = 1609;
        assertEquals(problemCount, problemsetProblems.getProblems().size());
        assertEquals(problemCount, problemsetProblems.getProblemStatistics().size());
        Problem problem = problemsetProblems.getProblems().get(0);
        assertEquals("You Are Given Some Letters...", problem.getName());
        assertEquals(2800L, (long) problem.getRating());
    }

    @Test
    void getProblemsetRecentStatus() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("problemset.recentStatus"),
                argThat(arg -> Objects.equals(arg.getFirst("count"), "10"))))
                .thenReturn(loadFile("/mock/problemsetRecentStatus.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        ProblemsetRecentStatusParams params = ProblemsetRecentStatusParams.builder()
                .count(10L)
                .build();

        List<Submission> recentStatus = methods.getProblemsetRecentStatus(params);
        assertNotNull(recentStatus);

        assertEquals(10, recentStatus.size());
        Submission submission = recentStatus.get(0);
        assertEquals("GNU C++17", submission.getProgrammingLanguage());
        assertEquals(Submission.Testset.TESTS, submission.getTestset());
        assertEquals(58549003L, (long) submission.getId());
    }

    @Test
    void getRecentActions() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("recentActions"),
                argThat(arg -> Objects.equals(arg.getFirst("maxCount"), "30"))))
                .thenReturn(loadFile("/mock/recentActions.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<RecentAction> recentActions = methods.getRecentActions(30);
        assertNotNull(recentActions);

        assertEquals(30, recentActions.size());
        assertEquals(1565462185L, (long) recentActions.get(0).getTimeSeconds());
        Comment comment = recentActions.get(0).getComment();
        assertEquals("<div class=\"ttypography\"><p>as will this comment</p></div>", comment.getText());
    }

    @Test
    void getUserBlogEntries() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("user.blogEntries"),
                argThat(arg -> Objects.equals(arg.getFirst("handle"), "Sehnsucht"))))
                .thenReturn(loadFile("/mock/userBlogEntries.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<BlogEntry> blogEntries = methods.getUserBlogEntries("Sehnsucht");
        assertNotNull(blogEntries);

        assertEquals(5, blogEntries.size());
        BlogEntry blogEntry = blogEntries.get(2);
        assertEquals("Sehnsucht", blogEntry.getAuthorHandle());
        assertEquals("<p>Statistics of programming languages in Codeforces</p>", blogEntry.getTitle());
        assertEquals(184L, (long) blogEntry.getRating());
        assertEquals(1443200726L, (long) blogEntry.getCreationTimeSeconds());
    }

    @Test
    void getUserInfo() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("user.info"),
                argThat(arg -> Objects.equals(arg.getFirst("handles"), "Sehnsucht"))))
                .thenReturn(loadFile("/mock/userInfo.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<User> users = methods.getUserInfo(Collections.singletonList("Sehnsucht"));
        assertNotNull(users);

        User user = users.get(0);
        assertEquals("Sehnsucht", user.getHandle());
        assertEquals("Russia", user.getCountry());
        assertEquals(75L, (long) user.getFriendOfCount());
    }

    @Test
    void getUserRatedList() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("user.ratedList"),
                argThat(arg -> Objects.equals(arg.getFirst("activeOnly"), "true"))))
                .thenReturn(loadFile("/mock/userRatedList.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<User> users = methods.getUserRatedList(true);
        assertNotNull(users);

        assertEquals(24919, users.size());
        User user = users.get(0);
        assertEquals("tourist", user.getHandle());
    }

    @Test
    void getUserRating() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("user.rating"),
                argThat(arg -> Objects.equals(arg.getFirst("handle"), "Sehnsucht"))))
                .thenReturn(loadFile("/mock/userRating.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<RatingChange> ratingChanges = methods.getUserRating("Sehnsucht");
        assertNotNull(ratingChanges);

        assertEquals(119, ratingChanges.size());
        RatingChange change = ratingChanges.get(0);
        assertEquals(417L, (long) change.getContestId());
        assertEquals("RCC 2014 Warmup (Div. 2)", change.getContestName());
        assertEquals(1500L, (long) change.getOldRating());
    }

    @Test
    void getUserStatus() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(eq("user.status"), any()))
                .thenReturn(loadFile("/mock/userStatus.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        UserStatusParams params = UserStatusParams.builder()
                .handle("Sehnsucht")
                .from(1L)
                .count(10L)
                .build();

        List<Submission> submissions = methods.getUserStatus(params);
        assertNotNull(submissions);

        assertEquals(10, submissions.size());
        Submission submission = submissions.get(0);
        List<Member> members = submission.getAuthor().getMembers();
        assertEquals("Sehnsucht", members.get(0).getHandle());
        assertEquals("dani_bw", members.get(1).getHandle());
        assertEquals("TaTaPiH", members.get(2).getHandle());
    }

    @Test
    void getBlogEntryViewFailed() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("blogEntry.view"),
                argThat(arg -> Objects.equals(arg.getFirst("blogEntryId"), "999999999999"))))
                .thenReturn(loadFile("/mock/blogEntryViewFailed.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert results
        BlogEntry entry = methods.getBlogEntryView(999999999999L);
        assertNull(entry);
    }

    @Test
    void getBlogEntryViewServerCrash() {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("blogEntry.view"),
                argThat(arg -> Objects.equals(arg.getFirst("blogEntryId"), "79"))))
                .thenReturn(null);

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert results
        BlogEntry entry = methods.getBlogEntryView(79L);
        assertNull(entry);
    }

    @Test
    void getContestHacksFailed() throws IOException {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("contest.hacks"),
                argThat(arg -> Objects.equals(arg.getFirst("contestId"), "-1"))))
                .thenReturn(loadFile("/mock/contestHacksFailed.json"));

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<Hack> hacks = methods.getContestHacks(-1L);
        assertTrue(hacks.isEmpty());
    }

    @Test
    void getContestHacksServerCrash() {
        // Mock Codeforces API
        Mockito.when(apiConsumer.sendQuery(
                eq("contest.hacks"),
                argThat(arg -> Objects.equals(arg.getFirst("contestId"), "566"))))
                .thenReturn(null); // crashed codeforces server is likely to produce null on every query

        ApiMethods methods = new ApiMethods(apiConsumer, jacksonMapper);

        // Assert result
        List<Hack> hacks = methods.getContestHacks(566L);
        assertTrue(hacks.isEmpty());
    }

    private String loadFile(String path) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(path),
                StandardCharsets.UTF_8
        );
    }
}