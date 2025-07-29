package org.example.footballmanager.dto;

import lombok.Data;

@Data
public class TransferRequestDto {
    private Long playerId;
    private Long buyingTeamId;
}