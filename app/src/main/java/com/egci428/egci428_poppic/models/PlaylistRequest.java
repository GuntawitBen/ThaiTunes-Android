package com.egci428.egci428_poppic.models;

import java.util.Objects;

public class PlaylistRequest {
    private String user;
    private String songName;
    private String artistName;
    private String artWorkURL;
    private int playListNumber;

    public PlaylistRequest(String user, String songName, String artistName, String artWorkURL, int playListNumber) {
        this.user = user;
        this.songName = songName;
        this.artistName = artistName;
        this.artWorkURL = artWorkURL;
        this.playListNumber = playListNumber;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtWorkURL() {
        return artWorkURL;
    }

    public void setArtWorkURL(String artWorkURL) {
        this.artWorkURL = artWorkURL;
    }

    public int getPlayListNumber() {
        return playListNumber;
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
                "user='" + user + '\'' +
                ", songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", artWorkURL='" + artWorkURL + '\'' +
                ", playListNumber=" + playListNumber +
                '}';
    }

}