package com.prudhvi.musicworld.Model;


public class Song{

    private String Title, Album, Artist, Duration, Path, Name, AlbumID;


    public Song(String title, String album, String artist, String duration, String path, String name, String albumID) {
        Title = title;
        Album = album;
        Artist = artist;
        Duration = duration;
        Path = path;
        Name = name;
        AlbumID = albumID;
    }



    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAlbumID() {
        return AlbumID;
    }

    public void setAlbumID(String albumID) {
        AlbumID = albumID;
    }
}
