package ua.com.juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.juja.microservices.Utils;
import ua.com.juja.microservices.teams.dao.feign.KeepersClient;

import javax.inject.Inject;
import java.util.Collections;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 * @author Vladimir Zadorozhniy
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class TeamControllerIntegrationTest extends BaseIntegrationTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final String teamsActivateTeamUrl = "/v1/teams";
    private final String teamsDeactivateTeamUrl = "/v1/teams";
    private final String teamsGetAllTeamsUrl = "/v1/teams";
    @Value("${keepers.direction.teams}")
    private String teamsDirection;

    @MockBean
    private KeepersClient keepersClient;

    @Inject
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json")
    public void activateTeamIfUserNotInActiveTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json")));
        when(keepersClient.getDirections("uuid-from"))
                .thenReturn(Collections.singletonList(teamsDirection));
        mockMvc.perform(post(teamsActivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json")
    public void activateTeamIfUserInActiveTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestActivateTeamIfUsersInActiveTeamThrowsExceptions.json")));
        when(keepersClient.getDirections("uuid-from"))
                .thenReturn(Collections.singletonList(teamsDirection));
        mockMvc.perform(post(teamsActivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestDeactivateTeamIfUserInTeamExecutedCorrectly.json")));
        when(keepersClient.getDirections("uuid-from"))
                .thenReturn(Collections.singletonList(teamsDirection));
        mockMvc.perform(put(teamsDeactivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserNotInTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestDeactivateTeamIfUserNotInTeamThrowsException.json")));
        when(keepersClient.getDirections("uuid-from"))
                .thenReturn(Collections.singletonList(teamsDirection));
        mockMvc.perform(put(teamsDeactivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestDeactivateTeamIfUserInSeveralTeamsException.json")));
        when(keepersClient.getDirections("uuid-from"))
                .thenReturn(Collections.singletonList(teamsDirection));
        mockMvc.perform(put(teamsDeactivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAllActiveTeamsDataSet.json")
    public void getAllTeamsExecutedCorrectly() throws Exception {
        mockMvc.perform(get(teamsGetAllTeamsUrl)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }
}