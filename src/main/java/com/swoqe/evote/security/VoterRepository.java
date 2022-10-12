package com.swoqe.evote.security;

import com.swoqe.evote.security.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoterRepository extends JpaRepository<Voter, UUID> {
    Optional<Voter> findByUsername(String username);
}
