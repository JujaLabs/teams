package ua.com.juja.microservices.teams.dao.impl;

import feign.FeignException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ua.com.juja.microservices.teams.dao.KeeperRepository;
import ua.com.juja.microservices.teams.dao.feign.KeeperClient;
import ua.com.juja.microservices.teams.exceptions.KeeperExchangeException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestKeeperRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Inject
    private KeeperRepository keeperRepository;
    @MockBean
    private KeeperClient keeperClient;

    @Value("${keepers.endpoint.getDirections}")
    private String keepersGetDirectionsUrl;

    @Value("${keepers.direction.teams}")
    private String teamsDirection;


    @Test
    public void getDirectionsExecutedCorrectly() {
        String uuid = "uuid";
        List<String> expected = Arrays.asList("First direction", "Second direction");
        when(keeperClient.getDirections(uuid)).thenReturn(expected);
        List<String> actual = keeperRepository.getDirections(uuid);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void getDirectionsKeepersServiceReturnErrorThrowsException() {
        String uuid = "uuid";
        FeignException feignException = mock(FeignException.class);
        when(keeperClient.getDirections(uuid)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn("Keepers service returns error");

        expectedException.expect(KeeperExchangeException.class);
        expectedException.expectMessage(containsString("Keepers service returns error"));

        keeperRepository.getDirections(uuid);
    }
}