package org.example.footballmanager.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;

    private Integer experienceMonths;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team currentTeam;
}