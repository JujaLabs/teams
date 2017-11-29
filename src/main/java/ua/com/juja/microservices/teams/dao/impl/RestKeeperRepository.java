package ua.com.juja.microservices.teams.dao.impl;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ua.com.juja.microservices.teams.dao.KeeperRepository;
import ua.com.juja.microservices.teams.dao.feign.KeepersClient;
import ua.com.juja.microservices.teams.exceptions.ApiErrorMessage;
import ua.com.juja.microservices.teams.exceptions.KeeperExchangeException;
import ua.com.juja.microservices.teams.utils.Utils;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Ivan Shapovalov
 */
@Repository
@Profile({"production", "default"})
public class RestKeeperRepository implements KeeperRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private KeepersClient keepersClient;

    @Override
    public List<String> getDirections(String uuid) {
        List<String> directions;
        logger.debug("Send request to keepers repository");
        try {
            directions = keepersClient.getDirections(uuid);
        } catch (FeignException ex) {
            ApiErrorMessage error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Keepers service returned error: [{}]", ex.getMessage());
            throw new KeeperExchangeException(error, "Keepers service returns error: " + ex.getMessage());
        }
        logger.debug("Received list of keeper's '{}' directions: {}", uuid, directions);
        return directions;
    }
}