package ua.com.juja.microservices.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.microservices.teams.dao.feign.KeepersClient;
import ua.com.juja.microservices.teams.dao.impl.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.impl.ActivateTeamRequest;
import ua.com.juja.microservices.teams.entity.impl.DeactivateTeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserNotTeamsKeeperException;
import ua.com.juja.microservices.teams.service.TeamService;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 * @author Vladimir Zadorozhniy
 */
@RunWith(SpringRunner.class)
public class TeamServiceIntegrationTest extends BaseIntegrationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final String teamsDirection = "teams";

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private TeamService teamService;

    @MockBean
    private KeepersClient keepersClient;

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUsersInAnotherActiveTeam.json",
            loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void activateTeamIfUserNumberNotFourThrowsExeption() {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from, new HashSet<>(Arrays.asList("uuid1",
                "uuid2", "uuid3")));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Activate team Request must contain '4' members");

        teamService.activateTeam(activateTeamRequest);
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json",
            loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void activateTeamIfUserNotInActiveTeamExecutedCorrectly() throws IOException {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from,
                new HashSet<>(Arrays.asList("new-uuid", "uuid100", "uuid200", "uuid300")));
        Team expected = new Team(from, activateTeamRequest.getMembers());
        when(keepersClient.getDirections(from))
                .thenReturn(Collections.singletonList(teamsDirection));
        Team actual = teamService.activateTeam(activateTeamRequest);

        expected.setId(actual.getId());
        assertThatJson(actual).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expected);
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUsersInAnotherActiveTeam.json",
            loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void activateTeamIfUserInAnotherTeamsThrowsExeption() throws JsonProcessingException {
        String from = "uuid-from";
        String uuid = "uuid-in-team";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from, new HashSet<>(Arrays.asList(uuid,
                "uuid100", "uuid200", "uuid300")));
        when(keepersClient.getDirections(from))
                .thenReturn(Collections.singletonList(teamsDirection));
        expectedException.expect(UserAlreadyInTeamException.class);
        expectedException.expectMessage(String.format("User(s) '#%s#' exist(s) in another teams", uuid));

        teamService.activateTeam(activateTeamRequest);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void getTeamIfUserInTeamExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuid = "uuid-in-one-team";
        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(1, teamsBefore.size());

        Team teamsAfter = teamService.getUserActiveTeam(uuid);
        assertEquals(teamsAfter, teamsBefore.get(0));
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void getTeamIfUserNotInTeamExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuid = "uuid-not-in-team";

        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);

        assertEquals(0, teamsBefore.size());
        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now", uuid));

        teamService.getUserActiveTeam(uuid);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void getTeamIfUserInSeveralTeamsExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuid = "uuid-in-several-teams";

        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);

        assertEquals(2, teamsBefore.size());
        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));

        teamService.getUserActiveTeam(uuid);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserNotKeeperTeamExecutedCorrectly() throws JsonProcessingException {
        String from = "uuid-from";
        String uuid = "uuid-in-one-team";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);
        when(keepersClient.getDirections(from))
                .thenReturn(new ArrayList<>());
        expectedException.expect(UserNotTeamsKeeperException.class);
        expectedException.expectMessage("User '#uuid-from#' have not permissions for that command");

        teamService.deactivateTeam(deactivateTeamRequest);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInTeamExecutedCorrectly() throws JsonProcessingException {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        String from = "uuid-from";
        String uuid = "uuid-in-one-team";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);
        when(keepersClient.getDirections(from))
                .thenReturn(Collections.singletonList(teamsDirection));
        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(1, teamsBefore.size());

        teamService.deactivateTeam(deactivateTeamRequest);

        actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        List<Team> teamsAfter = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(0, teamsAfter.size());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAllActiveTeamsDataSet.json")
    public void getAllTeamsExecutedCorrectly() {
        final Team team1 = new Team("uuid-from", new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        final Team team2 = new Team("uuid-from", new HashSet<>(Arrays.asList("uuid5", "uuid6", "uuid7", "uuid8")));
        final List<Team> expected = Arrays.asList(team1, team2);

        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        List<Team> actual = teamRepository.getAllActiveTeams(actualDate);

        assertEquals(2, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i).getMembers(), is(expected.get(i).getMembers()));
        }
    }
}