package com.tinglabs.silent.party.util;

import com.jlubecki.soundcloud.webapi.android.models.Track;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.player.JcAudio;
import com.tinglabs.silent.party.ui.items.SharedTrackItem;

import java.util.ArrayList;
import java.util.List;

import com.tinglabs.silent.party.player.Origin;

/**
 * Created by Talal on 1/3/2017.
 */

public class MapperUtils {

    public static com.tinglabs.silent.party.model.Track from(JcAudio jcAudio, int trackStatePlaying) {
        com.tinglabs.silent.party.model.Track track = from(jcAudio);
        track.setState(trackStatePlaying);
        return track;
    }

    public static com.tinglabs.silent.party.model.Track from(JcAudio audio) {
        com.tinglabs.silent.party.model.Track track = new com.tinglabs.silent.party.model.Track();
        track.setTrackId(audio.getTrackId());
        track.setTitle(audio.getTitle());
        track.setArtist(audio.getArtist());
        track.setDescription(audio.getDescription());
        track.setGenre(audio.getGenre());
        track.setVotes(audio.getVotes());
        track.setStreamUrl(audio.getStreamUrl());
        track.setArtworkUrl(audio.getArtworkUrl());
        track.setSeek(audio.getSeek());
        track.setPlaylistId(audio.getPlaylistId());
        track.setState(audio.getState());
        return track;
    }

    public static List<JcAudio> jcAudiosFrom(List<com.tinglabs.silent.party.model.Track> trackList) {
        List<JcAudio> list = new ArrayList<>();
        for (com.tinglabs.silent.party.model.Track track : trackList) {
            list.add(from(track));
        }
        return list;
    }

    public static JcAudio from(com.tinglabs.silent.party.model.Track track) {
        JcAudio audio = new JcAudio(track.getTitle(), track.getStreamUrl(), Origin.URL);
        audio.setId(Long.parseLong(track.getTrackId()));
        audio.setTrackId(track.getTrackId());
        audio.setTitle(track.getTitle());
        audio.setArtist(track.getArtist());
        audio.setDescription(track.getDescription());
        audio.setGenre(track.getGenre());
        audio.setVotes(track.getVotes());
        audio.setStreamUrl(track.getStreamUrl());
        audio.setArtworkUrl(track.getArtworkUrl());
        audio.setSeek(track.getSeek());
        audio.setPlaylistId(track.getPlaylistId());
        audio.setState(track.getState());
        return audio;
    }

    public static List<SharedTrackItem> from(List<Track> trackList) {
        List<SharedTrackItem> sharedTrackItems = new ArrayList<>();
        for (Track track : trackList) {
            com.tinglabs.silent.party.model.Track t = new com.tinglabs.silent.party.model.Track();
            t.setTrackId(track.id);
            t.setTitle(track.title);
            t.setDescription(track.description);
            t.setStreamUrl(resolveUrl(track.stream_url));
            t.setArtworkUrl(track.artwork_url);
            sharedTrackItems.add(new SharedTrackItem(t));
        }
        return sharedTrackItems;
    }

    public static String resolveUrl(String streamUrl) {
        return streamUrl + "?client_id=" + Config.SOUND_CLOUD_ID;
    }
}
