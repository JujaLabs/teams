package ua.com.juja.microservices.teams.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.microservices.teams.dao.KeeperRepository;
import ua.com.juja.microservices.teams.dao.feign.KeepersClient;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Vladimir Zadorozhniy
 */
@RunWith(SpringRunner.class)
@WebMvcTest(KeeperService.class)
public class KeeperServiceTest {
    @Inject
    private KeeperService keeperService;

    @MockBean
    private KeeperRepository keeperRepository;

    @MockBean
    private KeepersClient keepersClient;

    @Test
    public void getDirections() {
        String from = "uuid-from";
        List<String> expected = Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4");
        when(keeperRepository.getDirections(from)).thenReturn(expected);

        List<String> actual = keeperService.getDirections(from);
        assertThat(actual, is(expected));
    }
}