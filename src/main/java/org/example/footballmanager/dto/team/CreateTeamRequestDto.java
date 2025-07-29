package org.example.footballmanager.dto.team;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateTeamRequestDto {
    @NotBlank()
    private String name;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal accountBalance;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal commissionPercentage;
}