package ua.com.juja.microservices.teams.dao.impl;

import feign.FeignException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.microservices.teams.dao.KeeperRepository;
import ua.com.juja.microservices.teams.dao.feign.KeepersClient;
import ua.com.juja.microservices.teams.exceptions.KeeperExchangeException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Ivan Shapovalov
 * @author Vladimir Zadorozhniy
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestKeeperRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Inject
    private KeeperRepository keeperRepository;
    @MockBean
    private KeepersClient keepersClient;

    @Test
    public void getDirectionsExecutedCorrectly() {
        String uuid = "uuid";
        List<String> expected = Arrays.asList("First direction", "Second direction");
        when(keepersClient.getDirections(uuid)).thenReturn(expected);
        List<String> actual = keeperRepository.getDirections(uuid);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void getDirectionsKeepersServiceReturnErrorThrowsException() {
        String uuid = "uuid";
        FeignException feignException = mock(FeignException.class);
        when(keepersClient.getDirections(uuid)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn("Keepers service returns error");

        expectedException.expect(KeeperExchangeException.class);
        expectedException.expectMessage(containsString("Keepers service returns error"));

        keeperRepository.getDirections(uuid);
    }
}