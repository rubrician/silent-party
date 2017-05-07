package com.tinglabs.silent.party.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.tinglabs.silent.party.conf.Config;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Talal on 12/26/2016.
 */

public class Track extends SugarRecord implements Serializable {

    // @Formatter:off
    @Ignore
    public static final String PROP_PLAYLIST_ID          = "playlist_id";
    @Ignore
    public static final String PROP_TRACK_ID             = "track_id";

    @Ignore
    public static final int TRACK_STATE_NONE = -1;
    @Ignore
    public static final int TRACK_STATE_PAUSED = 0;
    @Ignore
    public static final int TRACK_STATE_PLAYING = 1;
    // @Formatter:on

    private String trackId;
    private String title;
    private String artist;
    private String description;
    private String genre;
    private int votes = 0;
    private String streamUrl;
    private String artworkUrl;
    private long seek = 0; //millis
    private String playlistId;
    private int state = TRACK_STATE_NONE; // -1 none, 0 pause, 1 playing

    public Track() {
    }

    public Track(String trackId, String title, String description, String streamUrl, String artworkUrl) {
        this.trackId = trackId;
        this.title = title;
        this.description = description;
        this.streamUrl = streamUrl;
        this.artworkUrl = artworkUrl;
    }

    public Track(String trackId, String title, String description, String streamUrl, String artworkUrl, String playlistId) {
        this.trackId = trackId;
        this.title = title;
        this.description = description;
        this.streamUrl = streamUrl;
        this.artworkUrl = artworkUrl;
        this.playlistId = playlistId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void incrementVote() {
        this.votes++;
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

    public void addVote(int vote) {
        this.votes += vote;
    }

    public boolean isPlaying() {
        return state == TRACK_STATE_PLAYING;
    }

    public boolean isPaused() {
        return state == TRACK_STATE_PAUSED;
    }

    public static Iterator<Track> getAll(String playlistId) {
        return Select.from(Track.class).where(Condition.prop(Track.PROP_PLAYLIST_ID).eq(playlistId)).iterator();
    }

    public static List<Track> generateMyPlaylistTracks() {
        List<Track> list = Select.from(Track.class).where(Condition.prop(Track.PROP_PLAYLIST_ID).eq(Config.DEFAULT_MY_PLAYLIST)).list();
        if (list == null || list.isEmpty()) {
            list = Config.DEFAULT_TRACKS;
            SugarRecord.saveInTx(list);
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        if (!((Track) o).getTrackId().equals(trackId)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackId='" + trackId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", votes=" + votes +
                ", streamUrl='" + streamUrl + '\'' +
                ", artworkUrl='" + artworkUrl + '\'' +
                ", seek=" + seek +
                ", playlistId='" + playlistId + '\'' +
                "} " + super.toString();
    }
}
