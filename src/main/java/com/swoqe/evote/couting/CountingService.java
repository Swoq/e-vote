package com.swoqe.evote.couting;

import com.swoqe.evote.voting.dto.Ballot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountingService {
    private final List<Ballot> ballots = new LinkedList<>();

    public void addBallot(Ballot ballot) {
        ballots.add(ballot);
    }

    public List<Ballot> getAllBallots() {
        return ballots;
    }
}

