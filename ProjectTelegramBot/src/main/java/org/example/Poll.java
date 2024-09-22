package org.example;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Poll {
    private String creatorId;
    private List<Question> questions;
    private Map<String, List<String>> responses = new HashMap<>();
    private boolean active;

    public Poll(String creatorId, List<Question> questions) {
        this.creatorId = creatorId;
        this.questions = questions;
        this.active = true;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addResponse(String userId, List<String> answers) {
        responses.put(userId, answers);
    }

    public Map<String, List<String>> getResponses() {
        return responses;
    }



}

