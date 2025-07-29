package org.example.footballmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.footballmanager.dto.team.CreateTeamRequestDto;
import org.example.footballmanager.dto.team.TeamDto;
import org.example.footballmanager.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto createTeam(@RequestBody @Valid CreateTeamRequestDto dto) {
        return teamService.createTeam(dto);
    }

    @GetMapping("/{id}")
    public TeamDto getTeam(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    @GetMapping
    public Page<TeamDto> getAllTeams(Pageable pageable) {
        return teamService.getAllTeams(pageable);
    }

    @PutMapping("/{id}")
    public TeamDto updateTeam(@PathVariable Long id, @RequestBody @Valid CreateTeamRequestDto dto) {
        return teamService.updateTeam(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
    }
}