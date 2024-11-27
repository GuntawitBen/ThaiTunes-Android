package com.egci428.egci428_poppic.models;

public class Song {
    private String songName;
    private String artistName;
    private String artWorkURL;
    private String genre;

    // Getters and Setters
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artist) {
        this.artistName = artistName;
    }

    public String getArtWorkURL() {
        return artWorkURL;
    }

    public void setArtWorkURL(String artWorkURL) {
        this.artWorkURL = artWorkURL;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}

