package ua.com.juja.microservices.teams.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;

import ua.com.juja.microservices.teams.exceptions.ApiErrorMessage;

/**
 * @author Ivan Shapovalov
 */
public class Utils {
    public static ApiErrorMessage convertToApiError(String message) {
        int contentExists = message.indexOf("content:");
        ApiErrorMessage apiError = new ApiErrorMessage(
                500, "BotInternalError",
                "I'm, sorry. I cannot parse api error message from remote service :(",
                "Cannot parse api error message from remote service",
                message, Collections.singletonList(message));
        if (contentExists != -1) {
            String apiMessage = message.substring(contentExists + 8);
            ObjectMapper mapper = new ObjectMapper();
            try {
                apiError = mapper.readValue(apiMessage, ApiErrorMessage.class);
            } catch (IOException e) {
                apiError = new ApiErrorMessage(
                        500, "BotInternalError",
                        "I'm, sorry. I cannot parse api error message from remote service :(",
                        "Cannot parse api error message from remote service",
                        e.getMessage(), Collections.singletonList(message)
                );
            }
        }
        return apiError;
    }
}
