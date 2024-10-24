// File: Song.java
package com.example.musicapp;

public class Song {
    private int songResource;
    private String title;
    private String artist;
    private int albumArtResource;

    // Constructor
    public Song(int songResource, String title, String artist, int albumArtResource) {
        this.songResource = songResource;
        this.title = title;
        this.artist = artist;
        this.albumArtResource = albumArtResource;
    }

    // Getters
    public int getSongResource() {
        return songResource;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbumArtResource() {
        return albumArtResource;
    }
}
