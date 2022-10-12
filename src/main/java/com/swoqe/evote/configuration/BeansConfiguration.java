package com.swoqe.evote.configuration;

import com.swoqe.evote.voting.dto.Ballot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeansConfiguration {

    @Bean
    public Ballot onlyBallot() {
        Map<String, Boolean> candidates = new HashMap<>();
        candidates.put("candidate1", false);
        candidates.put("candidate2", false);
        candidates.put("candidate3", false);

        return new Ballot(candidates);
    }
}
