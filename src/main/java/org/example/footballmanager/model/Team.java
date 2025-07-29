package org.example.footballmanager.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal accountBalance;

    private BigDecimal commissionPercentage;

    @OneToMany(mappedBy = "currentTeam")
    private List<Player> players;
}