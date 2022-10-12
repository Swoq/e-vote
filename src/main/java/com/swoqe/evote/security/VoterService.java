package com.swoqe.evote.security;

import com.swoqe.evote.security.model.Voter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class VoterService implements UserDetailsService {

    private final VoterRepository voterRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return voterRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(format("User with username - %s, not found", username))
                );
    }

    public void saveVoter(Voter voter) {
        voterRepository.save(voter);
    }

}
