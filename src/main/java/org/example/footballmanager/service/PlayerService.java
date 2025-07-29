package org.example.footballmanager.service;

import org.example.footballmanager.dto.player.CreatePlayerRequestDto;
import org.example.footballmanager.dto.player.PlayerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlayerService {
    PlayerDto createPlayer(CreatePlayerRequestDto dto);

    PlayerDto getPlayerById(Long id);

    PlayerDto updatePlayer(Long id, CreatePlayerRequestDto dto);

    void deletePlayer(Long id);

    Page<PlayerDto> getAllPlayers(Pageable pageable);
}
