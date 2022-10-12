package com.swoqe.evote.voting.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class BallotParsingException extends JsonProcessingException {

    public final static String DEFAULT_EXCEPTION_MSG = """
            Error while validating ballot structure. Rules your ballot must stick:\s
            - Only one candidate is chosen;
            - List of candidates wasn't altered;
            - Structure of ballot wasn't altered;
            """;

    public BallotParsingException(Throwable rootCause) {
        super(rootCause);
    }
}
