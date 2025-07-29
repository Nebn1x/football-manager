package org.example.footballmanager.dto.team;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class TeamDto {
    private Long id;
    private String name;
    private BigDecimal accountBalance;
    private BigDecimal commissionPercentage;
}