package com.tinglabs.silent.party.player;

import android.support.annotation.RawRes;

import java.io.Serializable;

/**
 * Created by jean on 27/06/16.
 */

public class JcAudio implements Serializable {
    // @Formatter:on
    public static final int TRACK_STATE_NONE = -1;
    public static final int TRACK_STATE_PAUSED = 0;
    public static final int TRACK_STATE_PLAYING = 1;
    // @Formatter:on

    private long id;
    private String title;
    private int position;
    private String path;
    private Origin origin;

    private String trackId;
    private String artist;
    private String description;
    private String genre;
    private int votes = 0;
    private String streamUrl;//unique
    private String artworkUrl;
    private long seek = 0;
    private String playlistId;
    private int state = TRACK_STATE_NONE; // -1 none, 0 pause, 1 playing


    public JcAudio(String title, String path, Origin origin) {
        // It looks bad
        int randomNumber = path.length() + title.length();

        this.id = randomNumber;
        this.position = randomNumber;
        this.title = title;
        this.path = path;
        this.origin = origin;
    }

    public JcAudio(String title, String path, long id, int position, Origin origin) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.path = path;
        this.origin = origin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public long getSeek() {
        return seek;
    }

    public void setSeek(long seek) {
        this.seek = seek;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static JcAudio createFromRaw(@RawRes int rawId) {
        return new JcAudio(String.valueOf(rawId), String.valueOf(rawId), Origin.RAW);
    }

    public static JcAudio createFromRaw(String title, @RawRes int rawId) {
        return new JcAudio(title, String.valueOf(rawId), Origin.RAW);
    }

    public static JcAudio createFromAssets(String assetName) {
        return new JcAudio(assetName, assetName, Origin.ASSETS);
    }

    public static JcAudio createFromAssets(String title, String assetName) {
        return new JcAudio(title, assetName, Origin.ASSETS);
    }

    public static JcAudio createFromURL(String url) {
        return new JcAudio(url, url, Origin.URL);
    }

    public static JcAudio createFromURL(String title, String url) {
        return new JcAudio(title, url, Origin.URL);
    }

    public static JcAudio createFromFilePath(String filePath) {
        return new JcAudio(filePath, filePath, Origin.FILE_PATH);
    }

    public static JcAudio createFromFilePath(String title, String filePath) {
        return new JcAudio(title, filePath, Origin.FILE_PATH);
    }
}