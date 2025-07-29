package org.example.footballmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.footballmanager.dto.player.CreatePlayerRequestDto;
import org.example.footballmanager.dto.player.PlayerDto;
import org.example.footballmanager.service.PlayerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerDto createPlayer(@RequestBody @Valid CreatePlayerRequestDto dto) {
        return playerService.createPlayer(dto);
    }

    @GetMapping("/{id}")
    public PlayerDto getPlayer(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    @GetMapping
    public Page<PlayerDto> getAllPlayers(Pageable pageable) {
        return playerService.getAllPlayers(pageable);
    }

    @PutMapping("/{id}")
    public PlayerDto updatePlayer(@PathVariable Long id, @RequestBody @Valid CreatePlayerRequestDto dto) {
        return playerService.updatePlayer(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }
}