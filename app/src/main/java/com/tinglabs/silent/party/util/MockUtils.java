package com.tinglabs.silent.party.util;

import android.util.Pair;

import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Talal on 11/27/2016.
 */

public class MockUtils {

    private static Random rnd = new Random();
    public static List<Track> trackList = makeSongList();
    public static ArrayList<User> UserList = makeUserList();
    public static List<Playlist> PlayLists = makePlaylists();
    public static List<Party> PartyList = makePartyList();

    public static Party findParty(String id) {
        for (Party p : PartyList) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public static User getCurrentUser() {
        return UserList.get(0);
    }

    public static User getRandomUser() {
        int i = rnd.nextInt(1000);
        return new User("user_" + i, "user_" + i, "user_" + i + "@email"); // "user1 is the current user"
    }

    public static Pair<User, Party> createParty() {
        User user = getCurrentUser();
        user.setRole(User.Role.ORGANIZER);
        Party party = new Party(user.getUserName(), user.getUserName() + "-Party", makePlayList(), new ArrayList<User>());
        return new Pair<>(user, party);
    }

    private static List<Track> makeSongList() {
        List<Track> trackList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            trackList.add(new Track("track-" + i, "title-" + i, "descrption-" + i, "http://songurl-" + i, null));
        }
        return trackList;
    }

    private static ArrayList<User> makeUserList() {
        ArrayList<User> userList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            userList.add(new User("user_" + i, "user_" + i, "user_" + i + "@email")); // "user1 is the current user"
        }
        return userList;
    }

    private static List<Playlist> makePlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            playlists.add(new Playlist("playlist-" + i, trackList.subList(i, i + 10)));
        }
        return playlists;
    }

    private static ArrayList<Party> makePartyList() {
        ArrayList<Party> partyList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            partyList.add(new Party("party-" + i, getCurrentUser().getUserName(), PlayLists.get(i), new ArrayList<User>(UserList.subList(i, i + 10))));
        }
        return partyList;
    }

    private static Playlist makePlayList() {
        return new Playlist(getCurrentUser().getUserName() + "-Playlist", new ArrayList<Track>());
    }

}
