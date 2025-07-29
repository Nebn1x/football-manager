package org.example.footballmanager.dto.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDto {
    private Long id;
    private String name;
    private int age;
    private int experienceMonths;
    private Long teamId;
}