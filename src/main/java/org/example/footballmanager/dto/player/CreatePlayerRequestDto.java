package org.example.footballmanager.dto.player;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlayerRequestDto {
    @NotBlank()
    private String name;

    @Min(value = 18)
    private int age;

    @Min(value = 0)
    private int experienceMonths;

    private Long teamId;
}