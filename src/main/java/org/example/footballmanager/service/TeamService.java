package org.example.footballmanager.service;

import org.example.footballmanager.dto.team.CreateTeamRequestDto;
import org.example.footballmanager.dto.team.TeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TeamService {

    TeamDto createTeam(CreateTeamRequestDto dto);

    TeamDto getTeamById(Long id);

    TeamDto updateTeam(Long id, CreateTeamRequestDto dto);

    void deleteTeam(Long id);

    Page<TeamDto> getAllTeams(Pageable pageable);
}
