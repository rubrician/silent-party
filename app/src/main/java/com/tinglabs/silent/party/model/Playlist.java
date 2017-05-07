package com.tinglabs.silent.party.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Talal on 11/26/2016.
 */

public class Playlist extends SugarRecord implements Serializable {

    // @Formatter:off
    public static final String PROP_TITLE                     = "title";
    public static final String PROP_CURRENT_TRACK             = "current_track";
    public static final String PROP_TRACK_LIST                = "track_list";
    // @Formatter:on

    private String title;
    private List<Track> trackList;
    private Track currentTrack;

    public Playlist() {
    }

    public Playlist(String title, List<Track> trackList) {
        this.title = title;
        this.trackList = trackList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        this.currentTrack = currentTrack;
    }

    public List<Track> findTracks() {
        return Select.from(Track.class).where(Condition.prop(Track.PROP_PLAYLIST_ID).eq(this.title)).list();
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "title='" + title + '\'' +
                ", currentTrack=" + currentTrack +
                ", trackList=" + trackList +
                "} " + super.toString();
    }
}
