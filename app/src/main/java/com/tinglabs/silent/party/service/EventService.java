package com.tinglabs.silent.party.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rafakob.nsdhelper.NsdHelper;
import com.rafakob.nsdhelper.NsdListener;
import com.rafakob.nsdhelper.NsdService;
import com.rafakob.nsdhelper.NsdType;
import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.model.comm.JoinPartyResponse;
import com.tinglabs.silent.party.model.comm.NotifyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tinglabs.silent.party.api.PartyOrganizerAPI;
import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.model.comm.AddTrackRequest;
import com.tinglabs.silent.party.model.comm.AddTrackResponse;
import com.tinglabs.silent.party.model.comm.JoinPartyRequest;
import com.tinglabs.silent.party.model.comm.LeavePartyRequest;
import com.tinglabs.silent.party.model.comm.LeavePartyResponse;
import com.tinglabs.silent.party.model.comm.NotificationRequest;
import com.tinglabs.silent.party.model.comm.NotificationResponse;
import com.tinglabs.silent.party.model.comm.SyncPlaylistRequest;
import com.tinglabs.silent.party.model.comm.SyncPlaylistResponse;
import com.tinglabs.silent.party.model.comm.UpdateMemberRequest;
import com.tinglabs.silent.party.model.comm.UpdateMemberResponse;
import com.tinglabs.silent.party.model.comm.VoteTrackRequest;
import com.tinglabs.silent.party.model.comm.VoteTrackResponse;
import com.tinglabs.silent.party.server.MediaServer;
import com.tinglabs.silent.party.util.DanceEventHelper;
import com.tinglabs.silent.party.util.EventUtils;
import com.tinglabs.silent.party.util.RequestQueue;

/**
 * Created by Talal on 11/27/2016.
 */

public class EventService extends Service implements UserAPI, PartyMemberAPI, PartyOrganizerAPI, NsdListener {
    public static final String TAG = "EventService";

    public static final String ACTION_FORMAT = "com.tinglabs.silent.party.service.EventService.%s";
    public static final String EVENT_FORMAT = "com.tinglabs.silent.party.service.event.%s";

    public EventService() {
    }

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
    public static final String ACTION_TRACKS_LIST               = aft(PartyMemberAPI.ACTION_LIST_TRACKS);
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
    public static final String EVENT_PARTY_SERVICE_LOST         = eft("EVENT_PARTY_SERVICE_LOST");
    public static final String EVENT_CURRENT_PARTY              = eft("EVENT_CURRENT_PARTY");
    public static final String EVENT_TRACK_ADDED                = eft("EVENT_TRACK_ADDED");
    public static final String EVENT_TRACK_VOTED                = eft("EVENT_TRACK_VOTED");
    public static final String EVENT_CURRENT_TRACK              = eft("EVENT_CURRENT_TRACK");
    public static final String EVENT_TRACK_PAUSED               = eft("EVENT_TRACK_PAUSED");
    public static final String EVENT_TRACKS_LIST                = eft("EVENT_TRACK_LIST");
    public static final String EVENT_SYNC_PLAYLIST              = eft("EVENT_SYNC_PLAYLIST");
    public static final String EVENT_SYNC_TRACK                 = eft("EVENT_SYNC_TRACK");
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

    private User mUser = null;
    private Party mCurrentParty = Party.createUnknown();
    private HashMap<String, Party> mPartyMap = new HashMap<>();
    private MediaServer mServer;
    private NsdHelper mNsdHelper;
    private DanceEventHelper mDanceEventHelper;
    private ScheduledExecutorService mSyncScheduler;
    private ServiceHandler mServiceHandler;
    private BroadcastReceiver mReceiver = null;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            if (intent == null) return;
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
                Log.d(TAG, "Getting user status...");
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
            } else if (ACTION_TRACKS_LIST.equals(action)) {
                Log.d(TAG, "Getting tracks...");
                listTracks();
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
        mServiceHandler = new ServiceHandler(thread.getLooper());
        Log.d(TAG, "Service created...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        if (mReceiver != null) unregisterReceiver(mReceiver);
        if (mNsdHelper != null) mNsdHelper.unregisterService();
        if (mServer != null) mServer.stopServer();
        if (mDanceEventHelper != null) mDanceEventHelper.stop();
        if (mUser != null) mUser.save();
        if (mCurrentParty != null) mCurrentParty.save();
        Log.d(TAG, "Service destroyed...");
    }


    @Override
    public void createUser(User user) {
        mUser = user;
        mUser.save();
        sendEvent(new Intent(EVENT_USER_CREATED).putExtra("user", mUser));
    }

    @Override
    public void loadUser() {
        if (mUser == null) {
            mUser = User.first(User.class);
            mUser.setRole(User.Role.NONE); //Incase the app crashed and the role was still member/organizer
        }
        if (mNsdHelper == null) initNSD();
        Log.d(TAG, "Loaded " + mUser);
        sendEvent(new Intent(EVENT_USER_LOADED).putExtra("user", mUser));
    }

    @Override
    public void updateUser(User user) {
        mUser.setName(user.getName());
        mUser.setUserName(user.getUserName());
        mUser.setEmail(user.getEmail());
        mUser.setDescription(user.getDescription());
        mUser.setPicUrl(user.getPicUrl());
        mUser.update();
        Log.d(TAG, "Updated " + mUser.toString());
        sendEvent(new Intent(EVENT_USER_UPDATED).putExtra("user", mUser));
        updateMember(mUser); // Will also update user if he's in party
    }

    @Override
    public void createParty(Party party) {
        initOrganizer(party);
        mNsdHelper.unregisterService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNsdHelper.registerService(mCurrentParty.getName(), NsdType.HTTP);
            }
        }, 2000);
    }

    @Override
    public void userStatus() {
        String role = mUser == null ? User.Role.NONE : mUser.getRole();
        sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", role));
    }

    @Override
    public void quitParty() {
        if (mUser.isOrganizer()) {
            closeParty();
        } else if (mUser.isMember()) {
            leaveParty();
        }
    }

    @Override
    public void currentParty() {
        sendEvent(new Intent(EVENT_CURRENT_PARTY).putExtra("party", mCurrentParty));
    }

    @Override
    public void listMembers() {
        sendEvent(new Intent(EVENT_MEMBERS_LIST).putExtra("members", (ArrayList<User>) mCurrentParty.getMembersList()));
    }

    @Override
    public void findParties() {
        if (!mNsdHelper.isDiscoveryRunning()) {
            mNsdHelper.startDiscovery(NsdType.HTTP);
        }
    }

    @Override
    public void joinParty(Party party) {
        mCurrentParty = party;
        try {
            startLocalServer();
            JoinPartyRequest req = new JoinPartyRequest(mCurrentParty.getUrl(), mUser);
            RequestQueue.getInstance(this).addToRequestQueue(req.make(JoinPartyResponse.class,
                    new Response.Listener<JoinPartyResponse>() {
                        @Override
                        public void onResponse(JoinPartyResponse response) {
                            initMember(response.getPlaylist(), response.getMembers(), response.getMember(), response.getOrganizer());
                            sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", mUser.getRole()));
                            Log.d(TAG, "Server response: " + response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Log.e(TAG, "Server error: " + e.getMessage(), e);
                            sendErrorEvent(ACTION_JOIN_PARTY, e.getMessage());
                        }
                    }));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            sendErrorEvent(ACTION_JOIN_PARTY, e.getMessage());
        }
    }

    @Override
    public void leaveParty() {
        mDanceEventHelper.stop();
        mCurrentParty.setStatus(Party.Status.CLOSED);
        mUser.setRole(User.Role.NONE);
        mServer.stopPlaying();
        LeavePartyRequest req = new LeavePartyRequest(mCurrentParty.getUrl(), mUser);
        RequestQueue.getInstance(this).addToRequestQueue(req.make(LeavePartyResponse.class,
                new Response.Listener<LeavePartyResponse>() {
                    @Override
                    public void onResponse(LeavePartyResponse response) {
                        sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", mUser.getRole()));
                        Log.d(TAG, "Party left! " + mCurrentParty);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, e.getMessage(), e);
                        sendErrorEvent(ACTION_LEAVE_PARTY, e.getMessage());
                        sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", mUser.getRole()));
                    }
                }));
    }

    @Override
    public void closeParty() {
        mDanceEventHelper.stop();
        stopSyncing();
        mNsdHelper.unregisterService();
        mCurrentParty.setStatus(Party.Status.CLOSED);
        mUser.setRole(User.Role.NONE);
        mServer.stopPlaying();
        notifyMembers(new NotifyEvent(EVENT_PARTY_CLOSED));
        sendEvent(new Intent(EVENT_USER_ROLE).putExtra("role", mUser.getRole()));
        Log.d(TAG, "Party closed! " + mCurrentParty);
    }

    @Override
    public void syncPlaylist() {
        SyncPlaylistRequest req = new SyncPlaylistRequest(mCurrentParty.getUrl());
        RequestQueue.getInstance(this).addToRequestQueue(req.make(SyncPlaylistResponse.class,
                new Response.Listener<SyncPlaylistResponse>() {
                    @Override
                    public void onResponse(SyncPlaylistResponse response) {
                        mCurrentParty.setPlaylist(response.getPlaylist());
                        sendEvent(new Intent(EVENT_SYNC_PLAYLIST).putExtra("playlist", mCurrentParty.getPlaylist()));
                        Log.d(TAG, "Playlist synced!");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, e.getMessage(), e);
                        sendErrorEvent(ACTION_SYNC_PLAYLIST, e.getMessage());
                    }
                }));
    }

    @Override
    public void addTrack(Track track) {
        if (mUser.isOrganizer()) {
            mCurrentParty.addTrack(track);
            mServer.updateMediaPlayerList(mCurrentParty.getPlaylist());
            sendEvent(new Intent(EVENT_TRACK_ADDED).putExtra("track", track));
            notifyMembers(new NotifyEvent(EVENT_TRACK_ADDED, mCurrentParty.getPlaylist(), track));
        } else {
            AddTrackRequest req = new AddTrackRequest(mCurrentParty.getUrl(), mUser, track);
            RequestQueue.getInstance(this).addToRequestQueue(req.make(AddTrackResponse.class,
                    new Response.Listener<AddTrackResponse>() {
                        @Override
                        public void onResponse(AddTrackResponse response) {
                            Log.d(TAG, "Add track request made!");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Log.e(TAG, e.getMessage(), e);
                            sendErrorEvent(ACTION_ADD_TRACK, e.getMessage());
                        }
                    }));
        }
    }

    @Override
    public void voteTrack(Track track, int vote) {
        if (mUser.isOrganizer()) {
            Track votedTrack = mCurrentParty.voteTrack(track, vote);
            mCurrentParty.sortPlaylist();
            mServer.updateMediaPlayerList(mCurrentParty.getPlaylist());
            sendEvent(new Intent(EVENT_TRACK_VOTED).putExtra("track", votedTrack));
            notifyMembers(new NotifyEvent(EVENT_TRACK_VOTED, track, vote));
        } else {
            VoteTrackRequest req = new VoteTrackRequest(mCurrentParty.getUrl(), mUser, track, vote);
            RequestQueue.getInstance(this).addToRequestQueue(req.make(VoteTrackResponse.class,
                    new Response.Listener<VoteTrackResponse>() {
                        @Override
                        public void onResponse(VoteTrackResponse response) {
                            Log.d(TAG, "Vote track request made!");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Log.e(TAG, e.getMessage(), e);
                            sendErrorEvent(ACTION_VOTE_TRACK, e.getMessage());
                        }
                    }));
        }
    }

    @Override
    public void listTracks() {
        sendEvent(new Intent(EVENT_TRACKS_LIST).putExtra("tracks", (ArrayList<Track>) mCurrentParty.getPlaylist().getTrackList()));
    }

    @Override
    public void updateMember(User user) {
        if (mUser.isMember()) {
            UpdateMemberRequest req = new UpdateMemberRequest(mCurrentParty.getUrl(), mUser);
            RequestQueue.getInstance(this).addToRequestQueue(req.make(UpdateMemberResponse.class,
                    new Response.Listener<UpdateMemberResponse>() {
                        @Override
                        public void onResponse(UpdateMemberResponse response) {
                            sendEvent(new Intent(EVENT_MEMBER_UPDATED).putExtra("member", mUser));
                            Log.d(TAG, "Update request success!");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Log.e(TAG, e.getMessage(), e);
                            sendErrorEvent(ACTION_UPDATE_MEMBER, e.getMessage());
                        }
                    }));
        }
    }

    @Override
    public void kickMember(User member) {
        notifyMembers(new NotifyEvent(EVENT_MEMBER_KICKED, mCurrentParty.getMembersList(), member));
        mCurrentParty.removeMember(member);
        sendEvent(new Intent(EVENT_MEMBER_KICKED).putExtra("member", member));
    }

    @Override
    public void onNsdRegistered(NsdService nsdService) {
        // Set NSD details
        mCurrentParty.setHostIp(nsdService.getHostIp());
        mCurrentParty.setHostName(nsdService.getHostName());
        mCurrentParty.setUrl(EventUtils.resolveServerUrl(nsdService.getHostIp()));
        sendEvent(new Intent(EVENT_PARTY_CREATED).putExtra("party", mCurrentParty));
    }

    @Override
    public void onNsdDiscoveryFinished() {
        if (mPartyMap.size() == 0) {
            sendErrorEvent(ACTION_FIND_PARTIES, "No party found. Try refreshing!");
        }
    }

    @Override
    public void onNsdServiceFound(NsdService nsdService) {
        Log.d(TAG, "Found host ip " + nsdService.getHostName() + " host " + nsdService.getHost()); // TODO: remove afterwards
    }

    @Override
    public void onNsdServiceResolved(NsdService nsdService) {
        Party party = new Party();
        party.setName(nsdService.getName());
        party.setUrl(EventUtils.resolveServerUrl(nsdService.getHostIp()));
        party.setHostIp(nsdService.getHostIp());
        mPartyMap.put(party.getName(), party);
        sendEvent(new Intent(EVENT_NEW_PARTY_FOUND).putExtra("party", party));
        Log.d(TAG, "Resolved host name: " + nsdService.getHostName()); // TODO: remove afterwards
    }

    @Override
    public void onNsdServiceLost(NsdService nsdService) {
        Party party = mPartyMap.get(nsdService.getName());
        if (party == null) return;
        // If it is our current party then the organizer has left
        if (mCurrentParty.getName().equals(party.getName()) && mCurrentParty.isOpen()) {
            // TODO: This happens randomly even when organizer is always on network
//            mCurrentParty.setStatus(Party.Status.CLOSED);
//            mUser.setRole(User.Role.NONE);
//            mServer.stopPlaying();
//            sendEvent(new Intent(EVENT_PARTY_CLOSED).putExtra("party", party.getName()));
        } else {
            sendEvent(new Intent(EVENT_PARTY_SERVICE_LOST).putExtra("party", party.getName()));
        }
        mPartyMap.remove(nsdService.getName());
    }

    @Override
    public void onNsdError(String s, int i, String s1) {
        Log.d(TAG, "NSD Error: " + s + " : " + i + " : " + s1);
    }

    public Party getParty() {
        return mCurrentParty;
    }

    public User getUser() {
        return mUser;
    }

    public DanceEventHelper getDanceEventHelper() {
        return mDanceEventHelper;
    }

    public void sendErrorEvent(String action, String reason) {
        sendEvent(new Intent(EVENT_ERROR).putExtra("action", action).putExtra("reason", reason));
    }

    public void sendEvent(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, "Broadcasting event: " + intent.getAction());
    }

    public void notifyMembers(NotifyEvent notifyEvent) {
        Log.d(TAG, "Notifying members " + notifyEvent);
        Iterator<User> itr = mCurrentParty.getMembersList().listIterator();
        while (itr.hasNext()) {
            NotificationRequest req = new NotificationRequest(itr.next().getUrl(), notifyEvent);
            Log.d(TAG, "Sending notification to " + req.getUrl());
            RequestQueue.getInstance(this).addToRequestQueue(req.make(NotificationResponse.class,
                    new Response.Listener<NotificationResponse>() {
                        @Override
                        public void onResponse(NotificationResponse response) {
                            Log.d(TAG, "Notificaton sent: " + response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Log.e(TAG, "Error sending notification: " + e.getMessage(), e);
                        }
                    }));
        }
    }

    private void initMember(Playlist playlist, List<User> members, User member, User organizer) {
        mCurrentParty.setPlaylist(playlist);
        mCurrentParty.setMembersList(members);
        mCurrentParty.setOrganizer(organizer);
        mUser.setRole(User.Role.MEMBER);
        mUser.setUserIp(member.getUserIp());
        mUser.setUrl(EventUtils.resolveServerUrl(member.getUserIp()));
        mServer.initMemberPlayer();
        initDanceListener();
    }

    private void initOrganizer(Party party) {
        try {
            startLocalServer();
            mCurrentParty = party;
            mUser.setRole(User.Role.ORGANIZER);
            mCurrentParty.setOrganizer(mUser);
            mServer.initOrganizerPlayer();
            initDanceListener(); // TODO: remove afterwards - Organizer doesn't have this responsibility
            startSyncing();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            sendErrorEvent(ACTION_CREATE_PARTY, e.getMessage());
        }
    }

    private void initNSD() {
        mNsdHelper = new NsdHelper(this, this);
        mNsdHelper.setLogEnabled(true);
        mNsdHelper.setAutoResolveEnabled(true);
        mNsdHelper.setDiscoveryTimeout(Config.DEFAULT_DISCOVERY_TIMEOUT);
    }

    private void initDanceListener() {
        final Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mDanceEventHelper = new DanceEventHelper(this);
        mDanceEventHelper.setOnShakeListener(new DanceEventHelper.OnShakeListener() {
            public void onShake() {
                vibe.vibrate(100);
                if ((mCurrentParty.getPlaylist().getCurrentTrack() == null)) return;
                voteTrack(mCurrentParty.getPlaylist().getCurrentTrack(), 1);
            }
        });
    }

    private void startLocalServer() throws IOException {
        if (mServer == null)
            mServer = new MediaServer(this);
    }

    private void startSyncing() {
        mSyncScheduler = Executors.newScheduledThreadPool(5);
        mSyncScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Syncing...");
                if (mCurrentParty.getPlaylist().getCurrentTrack() == null) return;
                notifyMembers(new NotifyEvent(EVENT_SYNC_TRACK, mCurrentParty.getPlaylist().getCurrentTrack()));
            }
        }, 5, Config.DEFAULT_SYNC_TIME, TimeUnit.SECONDS);

    }

    private void stopSyncing() {
        mSyncScheduler.shutdown();
    }
}