package org.example.footballmanager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.footballmanager.dto.TransferRequestDto;
import org.example.footballmanager.exeption.InsufficientFundsException;
import org.example.footballmanager.model.Player;
import org.example.footballmanager.model.Team;
import org.example.footballmanager.repository.PlayerRepository;
import org.example.footballmanager.repository.TeamRepository;
import org.example.footballmanager.service.TransferService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @Transactional
    @Override
    public void performTransfer(TransferRequestDto dto) {
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new EntityNotFoundException("Player with id " + dto.getPlayerId() + " not found"));

        Team buyingTeam = teamRepository.findById(dto.getBuyingTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + dto.getBuyingTeamId() + " not found"));

        Team sellingTeam = player.getCurrentTeam();
        if (sellingTeam == null) {
            throw new InvalidDataAccessApiUsageException("Player is not currently assigned to any team");
        }

        if (sellingTeam.getId().equals(buyingTeam.getId())) {
            throw new IllegalStateException("Cannot transfer player to the same team");
        }

        if (player.getAge() <= 0) {
            throw new ArithmeticException("Player age must be greater than 0 to calculate transfer cost");
        }

        BigDecimal transferCost = BigDecimal.valueOf(player.getExperienceMonths())
                .multiply(BigDecimal.valueOf(100000))
                .divide(BigDecimal.valueOf(player.getAge()), 2, RoundingMode.HALF_UP);
        BigDecimal commission = transferCost.multiply(sellingTeam.getCommissionPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = transferCost.add(commission);

        if (buyingTeam.getAccountBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientFundsException("Buying team does not have enough funds. Required: " + totalAmount);
        }

        buyingTeam.setAccountBalance(buyingTeam.getAccountBalance().subtract(totalAmount));
        sellingTeam.setAccountBalance(sellingTeam.getAccountBalance().add(totalAmount));
        player.setCurrentTeam(buyingTeam);

        teamRepository.save(buyingTeam);
        teamRepository.save(sellingTeam);
        playerRepository.save(player);
    }
}