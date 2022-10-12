package com.swoqe.evote.voting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swoqe.evote.couting.CountingService;
import com.swoqe.evote.security.VoterService;
import com.swoqe.evote.security.model.Voter;
import com.swoqe.evote.voting.dto.Ballot;
import com.swoqe.evote.voting.exception.BallotDecryptionInvalidKeyException;
import com.swoqe.evote.voting.exception.BallotParsingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BallotService {

    private final KeyPair cvkKeyPair;
    private final Ballot onlyBallot;
    private final VoterService voterService;
    private final CountingService countingService;

    public void processEncrypted(byte[] ballotBytes) throws BallotParsingException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Voter userDetails = (Voter) authentication.getPrincipal();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(userDetails.getPublicKey());
            PublicKey userPublicKey = keyFactory.generatePublic(publicKeySpec);

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, cvkKeyPair.getPrivate());
            byte[] decryptedReadFileBytes = decryptCipher.doFinal(ballotBytes);
            decryptCipher.init(Cipher.DECRYPT_MODE, userPublicKey);
            byte[] decryptedTwiceReadFileBytes = decryptCipher.doFinal(decryptedReadFileBytes);

            ObjectMapper objectMapper = new ObjectMapper();
            Ballot ballot = objectMapper.readValue(new String(decryptedTwiceReadFileBytes, StandardCharsets.UTF_8), Ballot.class);
            if (!validateBallotInstance(ballot)) {
                throw new BallotParsingException(new IllegalArgumentException());
            }

            userDetails.setEnabled(false);
            voterService.saveVoter(userDetails);

            countingService.addBallot(ballot);
        } catch (JsonProcessingException e) {
            log.error(String.valueOf(e));
            throw new BallotParsingException(e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException e) {
            log.error(String.valueOf(e));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadPaddingException | InvalidKeyException e) {
            throw new BallotDecryptionInvalidKeyException(e);
        }
    }

    public boolean validateBallotInstance(Ballot ballot) {
        long countTrue = ballot.candidates().values().stream().filter(value -> value.equals(Boolean.TRUE)).count();
        return onlyBallot.candidates().keySet().equals(ballot.candidates().keySet()) && countTrue == 1;
    }
}
