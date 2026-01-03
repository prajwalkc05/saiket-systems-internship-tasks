package com.example.simplequizapp;

import java.util.List;

public class Question {
    private int id;
    private String text;
    private List<String> options;
    private String correctAnswer;

    public Question(int id, String text, List<String> options, String correctAnswer) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
