package com.swoqe.evote.voting.dto;

import java.util.Map;

public record Ballot(Map<String, Boolean> candidates) {
}
