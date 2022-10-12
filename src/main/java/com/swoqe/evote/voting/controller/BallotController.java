package com.swoqe.evote.voting.controller;

import com.swoqe.evote.couting.CountingService;
import com.swoqe.evote.voting.BallotService;
import com.swoqe.evote.voting.dto.Ballot;
import com.swoqe.evote.voting.exception.BallotDecryptionInvalidKeyException;
import com.swoqe.evote.voting.exception.BallotParsingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ballot")
@RequiredArgsConstructor
@Slf4j
public class BallotController {

    private final Ballot onlyBallot;
    private final BallotService ballotService;
    private final CountingService countingService;

    @GetMapping
    private Ballot getBallot() {
        return onlyBallot;
    }

    @PostMapping(value = "confirm", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    private ResponseEntity<String> confirmBallot(@RequestBody byte[] ballot) {
        try {
            ballotService.processEncrypted(ballot);
        } catch (BallotParsingException e) {
            log.error(String.valueOf(e));
            return new ResponseEntity<>(BallotParsingException.DEFAULT_EXCEPTION_MSG, HttpStatus.BAD_REQUEST);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getStatus());
        } catch (BallotDecryptionInvalidKeyException e) {
            return new ResponseEntity<>(BallotDecryptionInvalidKeyException.DEFAULT_EXCEPTION_MSG, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/count")
    private ResponseEntity<List<Ballot>> getCounting() {
        return new ResponseEntity<>(countingService.getAllBallots(), HttpStatus.OK);
    }
}
