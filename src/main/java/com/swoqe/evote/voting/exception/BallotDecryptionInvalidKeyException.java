package com.swoqe.evote.voting.exception;

public class BallotDecryptionInvalidKeyException extends RuntimeException {

    public final static String DEFAULT_EXCEPTION_MSG = "Keys cannot be matched to decrypt ballot. Check if you are using" +
            " your private key and then CVK public key to encrypt your ballot.";

    public BallotDecryptionInvalidKeyException(Throwable cause) {
        super(cause);
    }
}
