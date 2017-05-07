package com.tinglabs.silent.party.api;

import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 11/26/2016.
 */

public interface PartyMemberAPI {

//  having a locally stored and synchronized playlist (> consistent state)
//  synchronized listing of the current played song
//  adding songs to the playlist
//  voting songs on the playlist up/down (if I “dance”)
//  creating and updating a profile

    // @formatter:off
    String ACTION_SYNC_PLAYLIST           = "ACTION_SYNC_PLAYLIST";
    String ACTION_ADD_TRACK               = "ACTION_ADD_TRACK";
    String ACTION_VOTE_TRACK              = "ACTION_VOTE_TRACK";
    String ACTION_LIST_TRACKS             = "ACTION_LIST_TRACKS";
    String ACTION_UPDATE_MEMBER           = "ACTION_UPDATE_MEMBER";
    String ACTION_LEAVE_PARTY             = "ACTION_LEAVE_PARTY";
    String ACTION_LIST_MEMBERS            = "ACTION_LIST_MEMBERS";
    String ACTION_CURRENT_PARTY           = "ACTION_CURRENT_PARTY";
    // @formatter:on

    void syncPlaylist();

    void addTrack(Track track);

    void voteTrack(Track track, int vote); // -1 for DownVote, 1 for UpVote

    void listTracks();

    void updateMember(User user);

    void leaveParty();

    void listMembers();

    void currentParty();
}