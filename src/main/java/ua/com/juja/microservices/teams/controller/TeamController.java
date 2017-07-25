package ua.com.juja.microservices.teams.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.service.TeamService;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RestController
@RequestMapping(value = "/v1/teams")
@Slf4j
public class TeamController {

    @Inject
    private TeamService teamService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> activateTeam(@Valid @RequestBody TeamRequest request) {
        log.debug("Received 'Activate team' request {}", request);
        Team team = teamService.activateTeam(request);
        log.info("New team activated. Id: {}", team.getId());
        log.debug("Request 'Activate team' returned {}", team);
        return ResponseEntity.ok(team);
    }

    @PutMapping(value = "/users/{uuid}", produces = "application/json")
    public ResponseEntity<?> deactivateTeam(@PathVariable String uuid) {
        log.debug("Received 'Deactivate team' request. Deactivate team of user {}", uuid);
        Team team = teamService.deactivateTeam(uuid);
        log.info("Team deacticated. Team Id: {}", team.getId());
        log.debug("Request 'Deactivate team' returned team {}", team);
        return ResponseEntity.ok(team);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllActiveTeams() {
        //TODO Should be implemented feature TMF-F3
        return null;
    }

    @GetMapping(value = "/users/{uuid}", produces = "application/json")
    public ResponseEntity<?> getTeamByUuid(@PathVariable String uuid) {
        log.debug("Received 'Get team' request. Get team of user {}", uuid);
        Team team = teamService.getUserActiveTeam(uuid);
        log.info("Team content received. Team Id: {}", team.getId());
        log.debug("Request 'Get team' returned team {}", team);
        return ResponseEntity.ok(team);
    }
}
