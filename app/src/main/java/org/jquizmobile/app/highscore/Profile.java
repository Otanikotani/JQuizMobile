package org.jquizmobile.app.highscore;

import java.io.Serializable;

public class Profile implements Serializable {

    private String name;

    private long attempts;

    private String avatar;

    private Long highestScore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAttempts() {
        return attempts;
    }

    public void setAttempts(long attempts) {
        this.attempts = attempts;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Long highestScore) {
        this.highestScore = highestScore;
    }
}
