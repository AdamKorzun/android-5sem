package com.example.lab04;

import java.io.Serializable;

public class TableEntry implements Serializable {
    private String username;
    private String name;
    private int score;
    private long playingTime;
    private int position;

    public TableEntry(String username, String name, int score, long scoreLevels) {
        this.username = username;
        this.name = name;
        this.score = score;
        this.playingTime = scoreLevels;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getPlayingTime() {
        return playingTime;
    }

    public void setPlayingTime(long playingTime) {
        this.playingTime = playingTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
