package com.tinglabs.silent.party.util;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.tinglabs.silent.party.event.CreatePartyEventListener;
import com.tinglabs.silent.party.event.EventListener;
import com.tinglabs.silent.party.event.FindPartyEventListener;
import com.tinglabs.silent.party.event.PartyMemberEventListener;
import com.tinglabs.silent.party.event.PartyPlaylistEventListener;
import com.tinglabs.silent.party.event.StatusEventListener;
import com.tinglabs.silent.party.event.UserEventListener;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.service.EventService;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Talal on 1/12/2017.
 */
public class EventManager {

    public static final String TAG = "EventManager";

    private CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<>();

    private static EventManager instance = new EventManager();

    private EventManager() {
    }

    public static EventManager getInstance() {
        return instance;
    }

    public static void register(EventListener listener) {
        Log.d(TAG, "Registered " + listener.getClass());
        getInstance().listeners.add(listener);
    }

    public static void unregister(EventListener listener) {
        Log.d(TAG, "Unregistered " + listener.getClass());
        getInstance().listeners.remove(listener);
    }

    public static void process(Intent intent) {
        Log.d(TAG, "Processing " + intent.getAction());
        String action = intent.getAction();
        for (EventListener lt : getInstance().listeners) {
            if (lt instanceof Fragment && (((Fragment) lt)).getActivity() == null) { // For some reason if that happens
                getInstance().listeners.remove(lt);
            } else {
                if (EventService.EVENT_ERROR.equals(action)) {
                    lt.onError(intent.getStringExtra("action"), intent.getStringExtra("reason"));
                }
                if (lt instanceof UserEventListener) {
                    UserEventListener l = (UserEventListener) lt;
                    if (EventService.EVENT_USER_LOADED.equals(action))
                        l.userLoaded((User) intent.getSerializableExtra("user"));
                    else if (EventService.EVENT_USER_CREATED.equals(action))
                        l.userCreated((User) intent.getSerializableExtra("user"));
                    else if (EventService.EVENT_USER_UPDATED.equals(action))
                        l.userUpdated((User) intent.getSerializableExtra("user"));
                }
                if (lt instanceof FindPartyEventListener) {
                    FindPartyEventListener l = (FindPartyEventListener) lt;
                    if (EventService.EVENT_PARTIES_FOUND.equals(action))
                        l.partiesFound(((ArrayList<Party>) intent.getSerializableExtra("parties")));
                    else if (EventService.EVENT_NEW_PARTY_FOUND.equals(action))
                        l.partyFound((Party) intent.getSerializableExtra("party"));

                }
                if (lt instanceof CreatePartyEventListener) {
                    CreatePartyEventListener l = (CreatePartyEventListener) lt;
                    if (EventService.EVENT_PARTY_CREATED.equals(action))
                        l.partyCreated((Party) intent.getSerializableExtra("party"));
                }
                if (lt instanceof PartyMemberEventListener) {
                    PartyMemberEventListener l = (PartyMemberEventListener) lt;
                    if (EventService.EVENT_MEMBERS_LIST.equals(action))
                        l.memberList((ArrayList<User>) intent.getSerializableExtra("members"));
                    else if (EventService.EVENT_MEMBER_JOINED.equals(action))
                        l.memberJoined((User) intent.getSerializableExtra("member"));
                    else if (EventService.EVENT_MEMBER_LEFT.equals(action))
                        l.memberLeft((User) intent.getSerializableExtra("member"));
                    else if (EventService.EVENT_MEMBER_KICKED.equals(action))
                        l.memberKicked((User) intent.getSerializableExtra("member"));
                    else if (EventService.EVENT_MEMBER_UPDATED.equals(action))
                        l.memberUpdated((User) intent.getSerializableExtra("member"));
                }
                if (lt instanceof PartyPlaylistEventListener) {
                    PartyPlaylistEventListener l = (PartyPlaylistEventListener) lt;
                    if (EventService.EVENT_SYNC_PLAYLIST.equals(action))
                        l.syncPlaylist((Playlist) intent.getSerializableExtra("playlist"));
                    else if (EventService.EVENT_TRACK_ADDED.equals(action))
                        l.trackAdded((Track) intent.getSerializableExtra("track"));
                    else if (EventService.EVENT_TRACK_VOTED.equals(action))
                        l.trackVoted((Track) intent.getSerializableExtra("track"));
                    else if (EventService.EVENT_CURRENT_TRACK.equals(action))
                        l.currentTrack((Track) intent.getSerializableExtra("track"));
                    else if (EventService.EVENT_TRACKS_LIST.equals(action))
                        l.trackList((ArrayList<Track>) intent.getSerializableExtra("tracks"));
                }
                if (lt instanceof StatusEventListener) {
                    StatusEventListener l = (StatusEventListener) lt;
                    if (EventService.EVENT_CURRENT_PARTY.equals(action))
                        l.currentParty((Party) intent.getSerializableExtra("party"));
                    else if (EventService.EVENT_USER_ROLE.equals(action))
                        l.userRole(intent.getStringExtra("role"));
                    else if (EventService.EVENT_PARTY_CLOSED.equals(action))
                        l.partyClosed(intent.getStringExtra("party"), intent.getStringExtra("reason"));
                    else if (EventService.EVENT_PARTY_SERVICE_LOST.equals(action))
                        l.partyServiceLost(intent.getStringExtra("party"));
                }

                lt.onComplete(action);
            }
        }
    }
}
