package com.tinglabs.silent.party.model.comm;

import java.util.List;

import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 1/15/2017.
 */

public class NotifyEvent {

    private String name;
    private List<User> membersList;
    private Playlist playlist;

    private User onMember;
    private Track onTrack;
    private int onVote;

    public NotifyEvent(String name) {
        this.name = name;
    }

    public NotifyEvent(String name, List<User> membersList, User onMember) {
        this.name = name;
        this.membersList = membersList;
        this.onMember = onMember;
    }

    public NotifyEvent(String name, Playlist playlist, Track onTrack) {
        this.name = name;
        this.membersList = membersList;
        this.onTrack = onTrack;
    }

    public NotifyEvent(String name, Track onTrack) {
        this.name = name;
        this.onTrack = onTrack;
    }

    public NotifyEvent(String name, Track onTrack, int onVote) {
        this.name = name;
        this.onTrack = onTrack;
        this.onVote = onVote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembersList() {
        return membersList;
    }

    public void setMembersList(List<User> membersList) {
        this.membersList = membersList;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public User getOnMember() {
        return onMember;
    }

    public void setOnMember(User onMember) {
        this.onMember = onMember;
    }

    public Track getOnTrack() {
        return onTrack;
    }

    public void setOnTrack(Track onTrack) {
        this.onTrack = onTrack;
    }

    public int getOnVote() {
        return onVote;
    }

    public void setOnVote(int onVote) {
        this.onVote = onVote;
    }

    @Override
    public String toString() {
        return "NotifyEvent{" +
                "name='" + name + '\'' +
                ", membersList=" + membersList +
                ", playlist=" + playlist +
                ", onMember=" + onMember +
                ", onTrack=" + onTrack +
                ", onVote=" + onVote +
                '}';
    }
}
