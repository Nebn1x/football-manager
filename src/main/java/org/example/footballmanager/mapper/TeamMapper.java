package org.example.footballmanager.mapper;

import org.example.footballmanager.config.MapperConfig;
import org.example.footballmanager.dto.team.CreateTeamRequestDto;
import org.example.footballmanager.dto.team.TeamDto;
import org.example.footballmanager.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TeamMapper {
    @Mapping(target = "id", ignore = true)
    Team toEntity(CreateTeamRequestDto dto);

    TeamDto toDto(Team team);

    @Mapping(target = "id", ignore = true)
    void updateTeamFromDto(CreateTeamRequestDto dto, @MappingTarget Team team);
}