package com.tinglabs.silent.party.model.comm;

import java.util.List;

import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 1/2/2017.
 */

public class JoinPartyResponse extends AbstractResponse {

    private final Playlist playlist;
    private final List<User> members;
    private final User member;
    private final User organizer;

    public JoinPartyResponse(Boolean success, Playlist playlist, List<User> members, User member, User organizer) {
        super(success);
        this.playlist = playlist;
        this.members = members;
        this.member = member;
        this.organizer = organizer;
    }

    public List<User> getMembers() {
        return members;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public User getMember() {
        return member;
    }

    public User getOrganizer() {
        return organizer;
    }

    @Override
    public String toString() {
        return "JoinPartyResponse{" +
                "playlist=" + playlist +
                ", members=" + members +
                ", member=" + member +
                ", organizer=" + organizer +
                "} " + super.toString();
    }
}
