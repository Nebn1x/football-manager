package org.example.footballmanager;

import jakarta.persistence.EntityNotFoundException;
import org.example.footballmanager.dto.team.CreateTeamRequestDto;
import org.example.footballmanager.dto.team.TeamDto;
import org.example.footballmanager.mapper.TeamMapper;
import org.example.footballmanager.model.Team;
import org.example.footballmanager.repository.TeamRepository;
import org.example.footballmanager.service.impl.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTeam_ValidInput() {
        CreateTeamRequestDto dto = new CreateTeamRequestDto();
        dto.setName("Team A");
        dto.setAccountBalance(BigDecimal.valueOf(1000000));
        dto.setCommissionPercentage(BigDecimal.valueOf(5.0));

        Team team = new Team();
        team.setName("Team A");
        team.setAccountBalance(BigDecimal.valueOf(1000000));
        team.setCommissionPercentage(BigDecimal.valueOf(5.0));

        TeamDto teamDto = new TeamDto();
        teamDto.setId(1L);
        teamDto.setName("Team A");
        teamDto.setAccountBalance(BigDecimal.valueOf(1000000));
        teamDto.setCommissionPercentage(BigDecimal.valueOf(5.0));

        when(teamMapper.toEntity(dto)).thenReturn(team);
        when(teamRepository.save(team)).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(teamDto);

        TeamDto result = teamService.createTeam(dto);

        assertNotNull(result);
        assertEquals("Team A", result.getName());
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    public void testCreateTeam_InvalidName() {
        CreateTeamRequestDto dto = new CreateTeamRequestDto();
        dto.setName("");
        dto.setAccountBalance(BigDecimal.valueOf(1000000));
        dto.setCommissionPercentage(BigDecimal.valueOf(5.0));

        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(dto));
    }

    @Test
    public void testCreateTeam_NegativeBalance() {
        CreateTeamRequestDto dto = new CreateTeamRequestDto();
        dto.setName("Team A");
        dto.setAccountBalance(BigDecimal.valueOf(-100));
        dto.setCommissionPercentage(BigDecimal.valueOf(5.0));

        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(dto));
    }

    @Test
    public void testCreateTeam_NegativeCommission() {
        CreateTeamRequestDto dto = new CreateTeamRequestDto();
        dto.setName("Team A");
        dto.setAccountBalance(BigDecimal.valueOf(1000000));
        dto.setCommissionPercentage(BigDecimal.valueOf(-1.0));

        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(dto));
    }

    @Test
    public void testCreateTeam_CommissionZero() {
        CreateTeamRequestDto dto = new CreateTeamRequestDto();
        dto.setName("Team A");
        dto.setAccountBalance(BigDecimal.valueOf(1000000));
        dto.setCommissionPercentage(BigDecimal.ZERO);

        Team team = new Team();
        TeamDto teamDto = new TeamDto();
        when(teamMapper.toEntity(dto)).thenReturn(team);
        when(teamRepository.save(team)).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(teamDto);

        TeamDto result = teamService.createTeam(dto);
        assertNotNull(result);
    }

    @Test
    public void testGetTeamById_ExistingId() {
        Long id = 1L;
        Team team = new Team();
        team.setId(id);
        team.setName("Team A");

        TeamDto teamDto = new TeamDto();
        teamDto.setId(id);
        teamDto.setName("Team A");

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));
        when(teamMapper.toDto(team)).thenReturn(teamDto);

        TeamDto result = teamService.getTeamById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Team A", result.getName());
    }

    @Test
    public void testGetTeamById_NonExistingId() {
        Long id = 999L;
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> teamService.getTeamById(id));
    }

    @Test
    public void testUpdateTeam_ValidInput() {
        Long id = 1L;
        CreateTeamRequestDto dto = new CreateTeamRequestDto();
        dto.setName("Team B");
        dto.setAccountBalance(BigDecimal.valueOf(2000000));
        dto.setCommissionPercentage(BigDecimal.valueOf(3.0));

        Team team = new Team();
        team.setId(id);
        TeamDto teamDto = new TeamDto();
        teamDto.setId(id);

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(teamDto);

        TeamDto result = teamService.updateTeam(id, dto);

        assertNotNull(result);
        verify(teamMapper, times(1)).updateTeamFromDto(dto, team);
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    public void testDeleteTeam_ExistingId() {
        Long id = 1L;
        when(teamRepository.existsById(id)).thenReturn(true);

        teamService.deleteTeam(id);

        verify(teamRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteTeam_NonExistingId() {
        Long id = 999L;
        when(teamRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> teamService.deleteTeam(id));
    }
}