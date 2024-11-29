package com.egci428.egci428_poppic.models;

import java.util.Objects;

public class PlaylistRequest {
    private String user;
    private String songName;
    private int playListNumber;

    // Constructor
    public PlaylistRequest(String user, String songName, int playListNumber) {
        this.user = user;
        this.songName = songName;
        this.playListNumber = playListNumber;
    }

    // Getters
    public String getUser() {
        return user;
    }

    public String getSongName() {
        return songName;
    }

    public int getPlayListNumber() {
        return playListNumber;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setPlayListNumber(int playListNumber) {
        this.playListNumber = playListNumber;
    }


    // Override equals() and hashCode() for value comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistRequest that = (PlaylistRequest) o;
        return playListNumber == that.playListNumber &&
                user.equals(that.user) &&
                songName.equals(that.songName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, songName, playListNumber);
    }

    @Override
    public String toString() {
        return "PlaylistRequest{" +
                "userId='" + user + '\'' +
                ", songName='" + songName + '\'' +
                ", playListNumber=" + playListNumber +
                '}';
    }
}