package org.example.footballmanager.mapper;

import org.example.footballmanager.config.MapperConfig;
import org.example.footballmanager.dto.player.CreatePlayerRequestDto;
import org.example.footballmanager.dto.player.PlayerDto;
import org.example.footballmanager.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PlayerMapper {
    @Mapping(target = "id", ignore = true)
    Player toEntity(CreatePlayerRequestDto dto);

    PlayerDto toDto(Player player);

    @Mapping(target = "id", ignore = true)
    void updatePlayerFromDto(CreatePlayerRequestDto dto, @MappingTarget Player player);
}