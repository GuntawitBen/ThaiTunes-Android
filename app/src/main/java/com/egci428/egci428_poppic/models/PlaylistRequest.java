package com.egci428.egci428_poppic.models;

import java.util.Objects;

public class PlaylistRequest {
    private String userId;
    private String songName;
    private int playListNumber;
    private String artWorkURL;

    // Constructor
    public PlaylistRequest(String userId, String songName, int playListNumber, String artWorkURL) {
        this.userId = userId;
        this.songName = songName;
        this.playListNumber = playListNumber;
        this.artWorkURL = artWorkURL;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getSongName() {
        return songName;
    }

    public int getPlayListNumber() {
        return playListNumber;
    }

    public String getArtWorkURL() {
        return artWorkURL;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setPlayListNumber(int playListNumber) {
        this.playListNumber = playListNumber;
    }

    public void setArtWorkURL(String artWorkURL) {
        this.artWorkURL = artWorkURL;
    }

    // Override equals() and hashCode() for value comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistRequest that = (PlaylistRequest) o;
        return playListNumber == that.playListNumber &&
                userId.equals(that.userId) &&
                songName.equals(that.songName) &&
                artWorkURL.equals(that.artWorkURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, songName, playListNumber, artWorkURL);
    }

    // Override toString() to provide a string representation of the object
    @Override
    public String toString() {
        return "PlaylistRequest{" +
                "userId='" + userId + '\'' +
                ", songName='" + songName + '\'' +
                ", playListNumber=" + playListNumber +
                ", artWorkURL='" + artWorkURL + '\'' +
                '}';
    }
}