package org.example.footballmanager;

import jakarta.persistence.EntityNotFoundException;
import org.example.footballmanager.dto.TransferRequestDto;
import org.example.footballmanager.model.Player;
import org.example.footballmanager.model.Team;
import org.example.footballmanager.repository.PlayerRepository;
import org.example.footballmanager.repository.TeamRepository;
import org.example.footballmanager.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPerformTransfer_Success() {
        Long playerId = 1L;
        Long buyingTeamId = 2L;
        Long sellingTeamId = 1L;

        Player player = new Player();
        player.setId(playerId);
        player.setAge(25);
        player.setExperienceMonths(60);
        Team sellingTeam = new Team();
        sellingTeam.setId(sellingTeamId);
        sellingTeam.setCommissionPercentage(BigDecimal.valueOf(5.0));
        sellingTeam.setAccountBalance(BigDecimal.valueOf(1000000));
        player.setCurrentTeam(sellingTeam);

        Team buyingTeam = new Team();
        buyingTeam.setId(buyingTeamId);
        buyingTeam.setAccountBalance(BigDecimal.valueOf(2000000));

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(buyingTeamId)).thenReturn(Optional.of(buyingTeam));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        transferService.performTransfer(dto);

        // Transfer cost: (60 * 100000) / 25 = 240000
        // Commission: 240000 * 0.05 = 12000
        // Total: 240000 + 12000 = 252000
        // Buying team balance: 2000000 - 252000 = 1748000
        // Selling team balance: 1000000 + 252000 = 1252000
        assertEquals(0, BigDecimal.valueOf(1748000).compareTo(buyingTeam.getAccountBalance()));
        assertEquals(0, BigDecimal.valueOf(1252000).compareTo(sellingTeam.getAccountBalance()));
        assertEquals(buyingTeam, player.getCurrentTeam());

        verify(teamRepository, times(1)).save(buyingTeam);
        verify(teamRepository, times(1)).save(sellingTeam);
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    public void testPerformTransfer_PlayerNotFound() {
        Long playerId = 999L;
        Long buyingTeamId = 2L;

        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        assertThrows(EntityNotFoundException.class, () -> transferService.performTransfer(dto));
        verify(playerRepository, times(1)).findById(playerId);
        verifyNoMoreInteractions(teamRepository, playerRepository);
    }

    @Test
    public void testPerformTransfer_BuyingTeamNotFound() {
        Long playerId = 1L;
        Long buyingTeamId = 999L;

        Player player = new Player();
        player.setId(playerId);
        player.setCurrentTeam(new Team());

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(buyingTeamId)).thenReturn(Optional.empty());

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        assertThrows(EntityNotFoundException.class, () -> transferService.performTransfer(dto));
        verify(playerRepository, times(1)).findById(playerId);
        verify(teamRepository, times(1)).findById(buyingTeamId);
        verifyNoMoreInteractions(teamRepository, playerRepository);
    }

    @Test
    public void testPerformTransfer_PlayerHasNoTeam() {
        Long playerId = 1L;
        Long buyingTeamId = 2L;

        Player player = new Player();
        player.setId(playerId);
        player.setCurrentTeam(null);

        Team buyingTeam = new Team();
        buyingTeam.setId(buyingTeamId);
        buyingTeam.setAccountBalance(BigDecimal.valueOf(2000000));

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(buyingTeamId)).thenReturn(Optional.of(buyingTeam)); // Додано мок для buyingTeam

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        assertThrows(IllegalArgumentException.class, () -> transferService.performTransfer(dto));
        verify(playerRepository, times(1)).findById(playerId);
        verify(teamRepository, times(1)).findById(buyingTeamId);
        verifyNoMoreInteractions(teamRepository, playerRepository);
    }

    @Test
    public void testPerformTransfer_SameTeam() {
        Long playerId = 1L;
        Long buyingTeamId = 1L;

        Player player = new Player();
        player.setId(playerId);
        Team team = new Team();
        team.setId(buyingTeamId);
        player.setCurrentTeam(team);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(buyingTeamId)).thenReturn(Optional.of(team));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        assertThrows(IllegalArgumentException.class, () -> transferService.performTransfer(dto));
        verify(playerRepository, times(1)).findById(playerId);
        verify(teamRepository, times(1)).findById(buyingTeamId);
        verifyNoMoreInteractions(teamRepository, playerRepository);
    }

    @Test
    public void testPerformTransfer_InsufficientFunds() {
        Long playerId = 1L;
        Long buyingTeamId = 2L;

        Player player = new Player();
        player.setId(playerId);
        player.setAge(25);
        player.setExperienceMonths(60);
        Team sellingTeam = new Team();
        sellingTeam.setId(1L);
        sellingTeam.setCommissionPercentage(BigDecimal.valueOf(5.0));
        player.setCurrentTeam(sellingTeam);

        Team buyingTeam = new Team();
        buyingTeam.setId(buyingTeamId);
        buyingTeam.setAccountBalance(BigDecimal.valueOf(200000));

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(buyingTeamId)).thenReturn(Optional.of(buyingTeam));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        assertThrows(IllegalArgumentException.class, () -> transferService.performTransfer(dto));
        verify(playerRepository, times(1)).findById(playerId);
        verify(teamRepository, times(1)).findById(buyingTeamId);
        verifyNoMoreInteractions(teamRepository, playerRepository);
    }

    @Test
    public void testPerformTransfer_InvalidPlayerAge() {
        Long playerId = 1L;
        Long buyingTeamId = 2L;

        Player player = new Player();
        player.setId(playerId);
        player.setAge(0);
        player.setExperienceMonths(60);
        Team sellingTeam = new Team();
        sellingTeam.setId(1L);
        sellingTeam.setCommissionPercentage(BigDecimal.valueOf(5.0));
        player.setCurrentTeam(sellingTeam);

        Team buyingTeam = new Team();
        buyingTeam.setId(buyingTeamId);
        buyingTeam.setAccountBalance(BigDecimal.valueOf(2000000));

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(buyingTeamId)).thenReturn(Optional.of(buyingTeam));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setPlayerId(playerId);
        dto.setBuyingTeamId(buyingTeamId);

        assertThrows(IllegalArgumentException.class, () -> transferService.performTransfer(dto));
        verify(playerRepository, times(1)).findById(playerId);
        verify(teamRepository, times(1)).findById(buyingTeamId);
        verifyNoMoreInteractions(teamRepository, playerRepository);
    }
}