package com.tinglabs.silent.party.model;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Talal on 12/26/2016.
 */

public class Party extends SugarRecord implements Serializable {

    private String name = "Unknown-Cool-Party";
    private String hostIp;
    private String hostName;
    private String url;
    private Playlist playlist;
    private List<User> membersList;
    private String status = Status.OPEN;
    private User organizer;

    public class Status {
        public static final String OPEN = "OPEN";
        public static final String CLOSED = "CLOSED";
    }

    public Party() {
    }

    public Party(String name) {
        this.name = name;
    }

    public Party(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Party(String name, String hostIp, Playlist playlist, List<User> membersList) {
        this.name = name;
        this.hostIp = hostIp;
        this.playlist = playlist;
        this.membersList = membersList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public List<User> getMembersList() {
        return membersList;
    }

    public void setMembersList(List<User> membersList) {
        this.membersList = membersList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public static Party createUnknown() {
        return new Party("Unknown", Status.CLOSED);
    }

    public boolean isOpen() {
        return Status.OPEN.equals(status);
    }

    public Playlist sortPlaylist() {
        Collections.sort(playlist.getTrackList(), new Comparator<Track>() {
            @Override
            public int compare(Track lhs, Track rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getVotes() > rhs.getVotes() ? -1 : (lhs.getVotes() < rhs.getVotes()) ? 1 : 0;
            }
        });

        return playlist;
    }

    public Track voteTrack(Track track, int vote) {
        for (Track t : this.getPlaylist().getTrackList()) {
            if (t.getTrackId().equals(track.getTrackId())) {
                t.addVote(vote);
                return t;
            }
        }
        return track;
    }

    public void addTrack(Track track) {
        if (!playlist.getTrackList().contains(track)) playlist.getTrackList().add(track);
    }

    public void removeTrack(Track track) {
        playlist.getTrackList().remove(track);
    }

    public void updateMember(User user) {
        if (membersList == null || user == null) return;
        for (int i = 0; i < this.getMembersList().size(); i++) {
            if (getMembersList().get(i).getUserIp().equals(user.getUserIp())) {
                getMembersList().set(i, user);
                return;
            }
        }
    }

    public void addMember(User user) {
        if (membersList == null) return;
        if (!membersList.contains(user)) membersList.add(user);
    }

    public void removeMember(User user) {
        if (membersList == null || user == null) return;
        membersList.remove(user);
    }

    public User findMember(String userIp) {
        for (User u : this.getMembersList()) {
            if (u.getUserIp().equals(userIp)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Party{" +
                "name='" + name + '\'' +
                ", hostIp='" + hostIp + '\'' +
                ", hostName='" + hostName + '\'' +
                ", url='" + url + '\'' +
                ", playlist=" + playlist +
                ", membersList=" + membersList +
                ", status='" + status + '\'' +
                "} " + super.toString();
    }
}
