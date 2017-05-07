package com.tinglabs.silent.party.event;

import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;

import java.util.List;

/**
 * Created by Talal on 1/8/2017.
 */

public interface PartyPlaylistEventListener extends EventListener {

    void trackAdded(Track track);

    void trackVoted(Track track);

    void currentTrack(Track track);

    void trackList(List<Track> tracks);

    void syncPlaylist(Playlist playlist);
}
