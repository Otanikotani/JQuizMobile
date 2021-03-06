package org.jquizmobile.app.question;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private String questionText;

    private int difficulty;

    private boolean isMultiple;

    private List<String> topics;

    private List<Answer> answers;

    private String description;

    public Question() {

    }

    public void makeAnswerSelected(String answerText) {
        for (Answer answer : answers) {
            if (answer.getAnswerText().equals(answerText)) {
                answer.setIsSelected(true);
                break;
            }
        }
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
