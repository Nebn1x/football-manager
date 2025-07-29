package org.example.footballmanager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.footballmanager.dto.team.CreateTeamRequestDto;
import org.example.footballmanager.dto.team.TeamDto;
import org.example.footballmanager.mapper.TeamMapper;
import org.example.footballmanager.model.Team;
import org.example.footballmanager.repository.TeamRepository;
import org.example.footballmanager.service.TeamService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    public TeamDto createTeam(CreateTeamRequestDto dto) {
        validateTeamRequest(dto);
        Team team = teamMapper.toEntity(dto);
        return teamMapper.toDto(teamRepository.save(team));
    }

    @Override
    public TeamDto getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(teamMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + id + " not found"));
    }

    @Override
    public TeamDto updateTeam(Long id, CreateTeamRequestDto dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + id + " not found"));
        validateTeamRequest(dto);
        teamMapper.updateTeamFromDto(dto, team);
        return teamMapper.toDto(teamRepository.save(team));
    }

    @Override
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team with id " + id + " not found");
        }
        teamRepository.deleteById(id);
    }

    @Override
    public Page<TeamDto> getAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable)
                .map(teamMapper::toDto);
    }

    private void validateTeamRequest(CreateTeamRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InvalidDataAccessApiUsageException("Team name cannot be empty");
        }
        if (dto.getAccountBalance() == null || dto.getAccountBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Account balance cannot be negative");
        }
        if (dto.getCommissionPercentage() == null ||
                dto.getCommissionPercentage().compareTo(BigDecimal.ZERO) < 0 ||
                dto.getCommissionPercentage().compareTo(BigDecimal.TEN) > 0) {
            throw new ArithmeticException("Commission must be between 0 and 10%");
        }
    }
}
