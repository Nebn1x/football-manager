package org.example.footballmanager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.footballmanager.dto.player.CreatePlayerRequestDto;
import org.example.footballmanager.dto.player.PlayerDto;
import org.example.footballmanager.mapper.PlayerMapper;
import org.example.footballmanager.model.Player;
import org.example.footballmanager.model.Team;
import org.example.footballmanager.repository.PlayerRepository;
import org.example.footballmanager.repository.TeamRepository;
import org.example.footballmanager.service.PlayerService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final TeamRepository teamRepository;

    @Override
    public PlayerDto createPlayer(CreatePlayerRequestDto dto) {
        validatePlayerRequest(dto);
        Player player = playerMapper.toEntity(dto);
        if (dto.getTeamId() != null) {
            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Team with id " + dto.getTeamId() + " not found"));
            player.setCurrentTeam(team);
        }
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    public PlayerDto getPlayerById(Long id) {
        return playerRepository.findById(id)
                .map(playerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Player with id " + id + " not found"));
    }

    @Override
    public PlayerDto updatePlayer(Long id, CreatePlayerRequestDto dto) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Player with id " + id + " not found"));
        validatePlayerRequest(dto);
        playerMapper.updatePlayerFromDto(dto, player);
        if (dto.getTeamId() != null) {
            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Team with id " + dto.getTeamId() + " not found"));
            player.setCurrentTeam(team);
        } else {
            player.setCurrentTeam(null);
        }
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Player with id " + id + " not found");
        }
        playerRepository.deleteById(id);
    }

    @Override
    public Page<PlayerDto> getAllPlayers(Pageable pageable) {
        return playerRepository.findAll(pageable)
                .map(playerMapper::toDto);
    }

    private void validatePlayerRequest(CreatePlayerRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InvalidDataAccessApiUsageException("Player name cannot be empty");
        }
        if (dto.getAge() <= 0) {
            throw new ArithmeticException("Player age must be greater than 0");
        }
        if (dto.getExperienceMonths() < 0) {
            throw new IllegalStateException("Player experience cannot be negative");
        }
    }
}
