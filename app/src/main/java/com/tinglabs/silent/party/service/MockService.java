package com.tinglabs.silent.party.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.api.PartyOrganizerAPI;

import java.util.ArrayList;
import java.util.List;

import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.util.MockUtils;

/**
 * Created by Talal on 11/27/2016.
 */

public class MockService extends Service implements UserAPI, PartyOrganizerAPI, PartyMemberAPI {

    public static final String TAG = "MockService";
    public static final String ACTION_FORMAT = "com.tinglabs.silent.party.mockservice.%s";
    public static final String EVENT_FORMAT = "com.tinglabs.silent.party.service.event.%s";

    // @formatter:off
    private static String aft(String action) {return String.format(ACTION_FORMAT, action);}
    private static String eft(String event) {return String.format(EVENT_FORMAT, event);}

    // Actions
    public static final String ACTION_LOAD_USER                 = aft(UserAPI.ACTION_LOAD_USER);
    public static final String ACTION_UPDATE_USER               = aft(UserAPI.ACTION_UPDATE_USER);
    public static final String ACTION_FIND_PARTIES              = aft(UserAPI.ACTION_FIND_PARTIES);
    public static final String ACTION_JOIN_PARTY                = aft(UserAPI.ACTION_JOIN_PARTY);
    public static final String ACTION_CREATE_PARTY              = aft(UserAPI.ACTION_CREATE_PARTY);
    public static final String ACTION_USER_STATUS               = aft(UserAPI.ACTION_USER_STATUS);
    public static final String ACTION_USER_QUIT_PARTY           = aft(UserAPI.ACTION_USER_QUIT_PARTY);
    public static final String ACTION_SYNC_PLAYLIST             = aft(PartyMemberAPI.ACTION_SYNC_PLAYLIST);
    public static final String ACTION_ADD_TRACK                 = aft(PartyMemberAPI.ACTION_ADD_TRACK);
    public static final String ACTION_VOTE_TRACK                = aft(PartyMemberAPI.ACTION_VOTE_TRACK);
    public static final String ACTION_UPDATE_MEMBER             = aft(PartyMemberAPI.ACTION_UPDATE_MEMBER);
    public static final String ACTION_LEAVE_PARTY               = aft(PartyMemberAPI.ACTION_LEAVE_PARTY);
    public static final String ACTION_LIST_MEMBERS              = aft(PartyMemberAPI.ACTION_LIST_MEMBERS);
    public static final String ACTION_CURRENT_PARTY             = aft(PartyMemberAPI.ACTION_CURRENT_PARTY);
    public static final String ACTION_CLOSE_PARTY               = aft(PartyOrganizerAPI.ACTION_CLOSE_PARTY);
    public static final String ACTION_KICK_MEMBER               = aft(PartyOrganizerAPI.ACTION_KICK_MEMBER);
    // Events
    public static final String EVENT_PARTY_CREATED              = eft("EVENT_PARTY_CREATED");
    public static final String EVENT_PARTY_CLOSED               = eft("EVENT_PARTY_CLOSED");
    public static final String EVENT_PARTIES_FOUND              = eft("EVENT_PARTIES_FOUND");
    public static final String EVENT_NEW_PARTY_FOUND            = eft("EVENT_NEW_PARTY_FOUND");
    public static final String EVENT_CURRENT_PARTY              = eft("EVENT_CURRENT_PARTY");
    public static final String EVENT_TRACK_ADDED                = eft("EVENT_TRACK_ADDED");
    public static final String EVENT_TRACK_VOTED                = eft("EVENT_TRACK_VOTED");
    public static final String EVENT_TRACK_PLAYING              = eft("EVENT_CURRENT_TRACK");
    public static final String EVENT_SYNC_PLAYLIST              = eft("EVENT_SYNC_PLAYLIST");
    public static final String EVENT_MEMBERS_LIST               = eft("EVENT_MEMBERS_LIST");
    public static final String EVENT_MEMBER_JOINED              = eft("EVENT_MEMBER_JOINED");
    public static final String EVENT_MEMBER_LEFT                = eft("EVENT_MEMBER_LEFT");
    public static final String EVENT_MEMBER_UPDATED             = eft("EVENT_MEMBER_UPDATED");
    public static final String EVENT_MEMBER_KICKED              = eft("EVENT_MEMBER_KICKED");
    public static final String EVENT_USER_ROLE                  = eft("EVENT_USER_ROLE");
    public static final String EVENT_USER_LOADED                = eft("EVENT_USER_LOADED");
    public static final String EVENT_USER_CREATED               = eft("EVENT_USER_CREATED");
    public static final String EVENT_USER_UPDATED               = eft("EVENT_USER_UPDATED");
    public static final String EVENT_ERROR                      = eft("EVENT_ERROR");
    // @formatter:on

    private User user;
    private List<Party> partyList;
    private Party mCurrentParty;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            String action = intent.getAction();
            Log.d(TAG, "Action received: " + action);
            if (ACTION_LOAD_USER.equals(action)) {
                Log.d(TAG, "Loading user...");
                loadUser();
            } else if (ACTION_UPDATE_USER.equals(action)) {
                Log.d(TAG, "Updating user...");
                updateUser((User) intent.getSerializableExtra("user"));
            } else if (ACTION_CREATE_PARTY.equals(action)) {
                Log.d(TAG, "Creating party...");
                Party party = (Party) intent.getSerializableExtra("party");
                createParty(party);
            } else if (ACTION_FIND_PARTIES.equals(action)) {
                Log.d(TAG, "Finding parties...");
                findParties();
            } else if (ACTION_JOIN_PARTY.equals(action)) {
                Log.d(TAG, "Joining party...");
                Party party = (Party) intent.getSerializableExtra("party");
                joinParty(party);
            } else if (ACTION_LEAVE_PARTY.equals(action)) {
                Log.d(TAG, "Leaving party...");
                leaveParty();
            } else if (ACTION_LIST_MEMBERS.equals(action)) {
                Log.d(TAG, "Getting members...");
                listMembers();
            } else if (ACTION_USER_STATUS.equals(action)) {
                Log.d(TAG, "Getting mUser status...");
                userStatus();
            } else if (ACTION_USER_QUIT_PARTY.equals(action)) {
                Log.d(TAG, "Quitting partying...");
                quitParty();
            } else if (ACTION_SYNC_PLAYLIST.equals(action)) {
                Log.d(TAG, "Syncing playlist...");
                syncPlaylist();
            } else if (ACTION_ADD_TRACK.equals(action)) {
                Log.d(TAG, "Adding track...");
                addTrack((Track) intent.getSerializableExtra("track"));
            } else if (ACTION_VOTE_TRACK.equals(action)) {
                Log.d(TAG, "Voting track...");
                voteTrack((Track) intent.getSerializableExtra("track"), intent.getIntExtra("vote", 0));
            } else if (ACTION_UPDATE_MEMBER.equals(action)) {
                Log.d(TAG, "Updating member...");
                updateMember((User) intent.getSerializableExtra("member"));
            } else if (ACTION_CURRENT_PARTY.equals(action)) {
                Log.d(TAG, "Getting current party...");
                currentParty();
            } else if (ACTION_CLOSE_PARTY.equals(action)) {
                Log.d(TAG, "Closing party...");
                closeParty();
            } else if (ACTION_KICK_MEMBER.equals(action)) {
                Log.d(TAG, "Kicking member...");
                kickMember((User) intent.getSerializableExtra("member"));
            }
            // stopSelf(msg.arg1); //TODO: when to stop?
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, TAG + " starting", Toast.LENGTH_SHORT).show();
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = flags;
        msg.arg2 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, TAG + "service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void createUser(User user) {
        this.user = MockUtils.getCurrentUser();
    }

    @Override
    public void loadUser() {
        this.user = MockUtils.getCurrentUser();
    }

    @Override
    public void updateUser(User user) {
        this.user = user;
    }


    @Override
    public void createParty(Party party) {
        Pair<User, Party> p = MockUtils.createParty();
        this.mCurrentParty = p.second;
        this.user = p.first;
        sendEvent(new Intent(EVENT_PARTY_CREATED).putExtra("party", mCurrentParty));
    }

    @Override
    public void userStatus() {
        sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", this.user.getRole()));
    }


    @Override
    public void quitParty() {
        if (User.Role.ORGANIZER.equals(user.getRole())) {
            closeParty();
        } else if (User.Role.MEMBER.equals(user.getRole())) {
            leaveParty();
        }
        sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", this.user.getRole()));
    }

    @Override
    public void currentParty() {

    }

    @Override
    public void listMembers() {
        sendEvent(new Intent(EVENT_MEMBERS_LIST).putExtra("members", (ArrayList<User>) this.mCurrentParty.getMembersList()));
    }

    @Override
    public void findParties() {
        this.partyList = MockUtils.PartyList;
        sendEvent(new Intent(EVENT_PARTIES_FOUND).putExtra("parties", (ArrayList<Party>) partyList));
    }

    @Override
    public void joinParty(Party party) {

    }

    @Override
    public void syncPlaylist() {
        sendEvent(new Intent(EVENT_SYNC_PLAYLIST).putExtra("playlist", mCurrentParty.getPlaylist()));
    }

    @Override
    public void addTrack(Track track) {
        this.mCurrentParty.getPlaylist().getTrackList().add(track);
        sendEvent(new Intent(EVENT_TRACK_ADDED).putExtra("track", track));
    }

    @Override
    public void voteTrack(Track track, int vote) {
    }

    @Override
    public void listTracks() {
        sendEvent(new Intent(EVENT_MEMBERS_LIST).putExtra("tracks", (ArrayList<Track>) mCurrentParty.getPlaylist().getTrackList()));
    }

    @Override
    public void updateMember(User user) {

    }

    @Override
    public void leaveParty() {

    }

    @Override
    public void kickMember(User user) {
        this.mCurrentParty.removeMember(user);
        sendEvent(new Intent(EVENT_MEMBER_KICKED));
    }

    @Override
    public void closeParty() {
        this.mCurrentParty.setStatus(Party.Status.CLOSED);
        sendEvent(new Intent(EVENT_PARTY_CLOSED));
    }

    private void sendEvent(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, "Broadcasting event: " + intent.getAction());
    }
}
