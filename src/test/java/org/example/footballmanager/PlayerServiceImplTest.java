package org.example.footballmanager;

import jakarta.persistence.EntityNotFoundException;
import org.example.footballmanager.dto.player.CreatePlayerRequestDto;
import org.example.footballmanager.dto.player.PlayerDto;
import org.example.footballmanager.mapper.PlayerMapper;
import org.example.footballmanager.model.Player;
import org.example.footballmanager.model.Team;
import org.example.footballmanager.repository.PlayerRepository;
import org.example.footballmanager.repository.TeamRepository;
import org.example.footballmanager.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePlayer_ValidInput() {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto();
        dto.setName("Player 1");
        dto.setAge(25);
        dto.setExperienceMonths(60);
        dto.setTeamId(1L);

        Player player = new Player();
        Team team = new Team();
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(1L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerMapper.toEntity(dto)).thenReturn(player);
        when(playerRepository.save(player)).thenReturn(player);
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        PlayerDto result = playerService.createPlayer(dto);

        assertNotNull(result);
        verify(playerRepository, times(1)).save(player);
        verify(playerMapper, times(1)).toDto(player);
    }

    @Test
    public void testCreatePlayer_InvalidTeamId() {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto();
        dto.setName("Player 1");
        dto.setAge(25);
        dto.setExperienceMonths(60);
        dto.setTeamId(999L);

        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> playerService.createPlayer(dto));
    }

    @Test
    public void testCreatePlayer_InvalidName() {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto();
        dto.setName("");
        dto.setAge(25);
        dto.setExperienceMonths(60);

        assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(dto));
    }

    @Test
    public void testCreatePlayer_NegativeAge() {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto();
        dto.setName("Player 1");
        dto.setAge(-1);
        dto.setExperienceMonths(60);

        assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(dto));
    }

    @Test
    public void testGetPlayerById_ExistingId() {
        Long id = 1L;
        Player player = new Player();
        player.setId(id);
        player.setName("Player 1");

        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(id);
        playerDto.setName("Player 1");

        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        PlayerDto result = playerService.getPlayerById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Player 1", result.getName());
    }

    @Test
    public void testGetPlayerById_NonExistingId() {
        Long id = 999L;
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> playerService.getPlayerById(id));
    }

    @Test
    public void testUpdatePlayer_ValidInput() {
        Long id = 1L;
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto();
        dto.setName("Player 2");
        dto.setAge(26);
        dto.setExperienceMonths(48);
        dto.setTeamId(1L);

        Player player = new Player();
        player.setId(id);
        Team team = new Team();
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(id);

        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.save(player)).thenReturn(player);
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        PlayerDto result = playerService.updatePlayer(id, dto);

        assertNotNull(result);
        verify(playerMapper, times(1)).updatePlayerFromDto(dto, player);
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    public void testDeletePlayer_ExistingId() {
        Long id = 1L;
        when(playerRepository.existsById(id)).thenReturn(true);

        playerService.deletePlayer(id);

        verify(playerRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeletePlayer_NonExistingId() {
        Long id = 999L;
        when(playerRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> playerService.deletePlayer(id));
    }
}