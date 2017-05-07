package com.tinglabs.silent.party.model.comm;

import com.tinglabs.silent.party.model.Playlist;

/**
 * Created by Talal on 1/2/2017.
 */

public class SyncPlaylistResponse extends AbstractResponse {
    private Playlist playlist;

    public SyncPlaylistResponse(boolean success, Playlist playlist) {
        super(success);
        this.playlist = playlist;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public String toString() {
        return "SyncPlaylistResponse{" +
                "playlist=" + playlist +
                "} " + super.toString();
    }
}
