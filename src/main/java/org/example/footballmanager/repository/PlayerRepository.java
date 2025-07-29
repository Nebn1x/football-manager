package org.example.footballmanager.repository;

import org.example.footballmanager.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
