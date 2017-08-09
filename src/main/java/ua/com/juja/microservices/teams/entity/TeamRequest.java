package ua.com.juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.Set;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@ToString
public class TeamRequest {

    @NotEmpty
    private Set<String> members;

    @JsonCreator
    public TeamRequest(@JsonProperty("members") Set<String> members) {
        this.members = members;
    }

    public Set<String> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
