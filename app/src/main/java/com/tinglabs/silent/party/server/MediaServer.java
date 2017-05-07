package com.tinglabs.silent.party.server;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.model.comm.AbstractResponse;
import com.tinglabs.silent.party.model.comm.AddTrackRequest;
import com.tinglabs.silent.party.model.comm.AddTrackResponse;
import com.tinglabs.silent.party.model.comm.JoinPartyRequest;
import com.tinglabs.silent.party.model.comm.JoinPartyResponse;
import com.tinglabs.silent.party.model.comm.NotificationRequest;
import com.tinglabs.silent.party.model.comm.NotificationResponse;
import com.tinglabs.silent.party.model.comm.NotifyEvent;
import com.tinglabs.silent.party.model.comm.SyncPlaylistResponse;
import com.tinglabs.silent.party.model.comm.UpdateMemberResponse;
import com.tinglabs.silent.party.model.comm.VoteTrackRequest;
import com.tinglabs.silent.party.player.JcPlayerService;
import com.tinglabs.silent.party.service.EventService;
import com.tinglabs.silent.party.util.EventUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tinglabs.silent.party.model.comm.LeavePartyRequest;
import com.tinglabs.silent.party.model.comm.LeavePartyResponse;
import com.tinglabs.silent.party.model.comm.VoteTrackResponse;
import com.tinglabs.silent.party.player.JcAudioPlayer;
import com.tinglabs.silent.party.util.MapperUtils;
import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Talal on 1/21/2017.
 */

public class MediaServer implements JcPlayerService.JcPlayerServiceListener {
    public static final String TAG = "MediaServer";
    private final EventService svc;
    private JcAudioPlayer mPlayer;
    private LocalServer mServer;

    public MediaServer(EventService service) throws IOException {
        this.svc = service;
        mServer = new LocalServer(Config.DEFAULT_SERVER_PORT);
    }

    private class LocalServer extends NanoHTTPD {
        LocalServer(int port) throws IOException {
            super(port);
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Log.i(TAG, "Running media server...\n");
        }

        @Override
        public Response serve(IHTTPSession session) {
            Map<String, String> headers = session.getHeaders();
            String action = headers.get("action");
            String data = getData(session);
            Log.d(TAG, "Received " + action + " from " + session.getRemoteIpAddress() + " \n " + data);

            AbstractResponse response = null;
            //// Server actions ////
            if (UserAPI.ACTION_JOIN_PARTY.equals(action)) {
                JoinPartyRequest req = getRequest(JoinPartyRequest.class, data);
                User onMember = req.getUser();
                onMember.setUserIp(session.getRemoteIpAddress());
                onMember.setUrl(EventUtils.resolveServerUrl(session.getRemoteIpAddress()));
                svc.getParty().addMember(onMember);
                svc.sendEvent(new Intent(EventService.EVENT_MEMBER_JOINED).putExtra("member", onMember));
                svc.notifyMembers(new NotifyEvent(EventService.EVENT_MEMBER_JOINED, svc.getParty().getMembersList(), onMember));
                response = new JoinPartyResponse(true, svc.getParty().getPlaylist(),
                        svc.getParty().getMembersList(), onMember, svc.getParty().getOrganizer());
            } else if (PartyMemberAPI.ACTION_LEAVE_PARTY.equals(action)) {
                LeavePartyRequest req = getRequest(LeavePartyRequest.class, data);
                User onMember = req.getUser();
                svc.getParty().removeMember(onMember);
                svc.sendEvent(new Intent(EventService.EVENT_MEMBER_LEFT).putExtra("member", onMember));
                svc.notifyMembers(new NotifyEvent(EventService.EVENT_MEMBER_LEFT, svc.getParty().getMembersList(), onMember));
                response = new LeavePartyResponse(true);
            } else if (PartyMemberAPI.ACTION_UPDATE_MEMBER.equals(action)) {
                JoinPartyRequest req = getRequest(JoinPartyRequest.class, data);
                User onMember = req.getUser();
                onMember.setUserIp(session.getRemoteIpAddress());
                onMember.setUrl(EventUtils.resolveServerUrl(session.getRemoteIpAddress()));
                svc.getParty().updateMember(onMember);
                svc.sendEvent(new Intent(EventService.EVENT_MEMBER_UPDATED).putExtra("member", onMember));
                svc.notifyMembers(new NotifyEvent(EventService.EVENT_MEMBER_UPDATED, svc.getParty().getMembersList(), onMember));
                response = new UpdateMemberResponse(true);
            } else if (PartyMemberAPI.ACTION_ADD_TRACK.equals(action)) {
                AddTrackRequest req = getRequest(AddTrackRequest.class, data);
                svc.getParty().addTrack(req.getTrack());
                updateMediaPlayerList(svc.getParty().getPlaylist());
                svc.sendEvent(new Intent(EventService.EVENT_TRACK_ADDED).putExtra("track", req.getTrack()));
                svc.notifyMembers(new NotifyEvent(EventService.EVENT_TRACK_ADDED, svc.getParty().getPlaylist(), req.getTrack()));
                response = new AddTrackResponse(true);
            } else if (PartyMemberAPI.ACTION_VOTE_TRACK.equals(action)) {
                VoteTrackRequest req = getRequest(VoteTrackRequest.class, data);
                Track votedTrack = svc.getParty().voteTrack(req.getTrack(), req.getVote());
                svc.getParty().sortPlaylist();
                updateMediaPlayerList(svc.getParty().getPlaylist());
                svc.sendEvent(new Intent(EventService.EVENT_TRACK_VOTED).putExtra("track", votedTrack));
                svc.notifyMembers(new NotifyEvent(EventService.EVENT_TRACK_VOTED, req.getTrack(), req.getVote()));
                response = new VoteTrackResponse(true);
            } else if (PartyMemberAPI.ACTION_SYNC_PLAYLIST.equals(action)) {
                response = new SyncPlaylistResponse(true, svc.getParty().getPlaylist());
            }
            //// Client events ///
            else if (EventService.EVENT_MEMBER_JOINED.equals(action)) {
                NotifyEvent event = getNotification(data).getEvent();
                svc.getParty().setMembersList(event.getMembersList());
                svc.sendEvent(new Intent(EventService.EVENT_MEMBER_JOINED).putExtra("member", event.getOnMember()));
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_MEMBER_LEFT.equals(action)) {
                NotifyEvent event = getNotification(data).getEvent();
                svc.getParty().setMembersList(event.getMembersList());
                svc.sendEvent(new Intent(EventService.EVENT_MEMBER_LEFT).putExtra("member", event.getOnMember()));
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_MEMBER_UPDATED.equals(action)) {
                NotifyEvent event = getNotification(data).getEvent();
                svc.getParty().setMembersList(event.getMembersList());
                svc.sendEvent(new Intent(EventService.EVENT_MEMBER_UPDATED).putExtra("member", event.getOnMember()));
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_MEMBER_KICKED.equals(action)) {
                NotifyEvent event = getNotification(data).getEvent();
                Log.d(TAG, svc.getUser().getUserIp());
                if (event.getOnMember().getUserIp().equals(svc.getUser().getUserIp())) {
                    svc.getParty().setStatus(Party.Status.CLOSED);
                    svc.getUser().setRole(User.Role.NONE);
                    svc.sendEvent(new Intent(EventService.EVENT_USER_ROLE).putExtra("role", svc.getUser().getRole()));
                    stopPlaying();
                    makeToast("You have been kicked out from party!");
                } else {
                    svc.sendEvent(new Intent(EventService.EVENT_MEMBER_KICKED).putExtra("member", event.getOnMember()));
                }
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_TRACK_ADDED.equals(action)) {
                NotifyEvent event = getNotification(data).getEvent();
                svc.getParty().addTrack(event.getOnTrack());
                updateMediaPlayerList(svc.getParty().getPlaylist());
                svc.sendEvent(new Intent(EventService.EVENT_TRACK_ADDED).putExtra("track", event.getOnTrack()));
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_TRACK_VOTED.equals(action)) {
                NotifyEvent event = getNotification(data).getEvent();
                Track votedTrack = svc.getParty().voteTrack(event.getOnTrack(), event.getOnVote());
                svc.getParty().sortPlaylist();
                updateMediaPlayerList(svc.getParty().getPlaylist());
                svc.sendEvent(new Intent(EventService.EVENT_TRACK_VOTED).putExtra("track", votedTrack));
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_CURRENT_TRACK.equals(action)) {
                final NotifyEvent event = getNotification(data).getEvent();
                svc.getParty().getPlaylist().setCurrentTrack(event.getOnTrack());
                if (event.getOnTrack().isPlaying()) startPlaying(event.getOnTrack());
                else pausePlaying();
                svc.sendEvent(new Intent(EventService.EVENT_CURRENT_TRACK).putExtra("track", event.getOnTrack()));
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_SYNC_TRACK.equals(action)) {
                final NotifyEvent event = getNotification(data).getEvent();
                syncTrack((int) event.getOnTrack().getSeek());
                response = new NotificationResponse(true);
            } else if (EventService.EVENT_PARTY_CLOSED.equals(action)) {
                stopPlaying();
                svc.getParty().setStatus(Party.Status.CLOSED);
                svc.getUser().setRole(User.Role.NONE);
                svc.getDanceEventHelper().stop();
                svc.sendEvent(new Intent(EventService.EVENT_PARTY_CLOSED).putExtra("name", svc.getParty().getHostIp()).putExtra("reason", "unknown"));
                svc.sendEvent(new Intent(EventService.EVENT_USER_ROLE).putExtra("role", svc.getUser().getRole()));
                response = new NotificationResponse(true);
            }

            Log.d(TAG, "Sending response: " + response);
            return newFixedLengthResponse(response.toJson());
        }

        private String getData(IHTTPSession session) {
            String s = "";
            try {
                HashMap<String, String> data = new HashMap<>();
                session.parseBody(data);
                s = data.get("postData");
            } catch (IOException | ResponseException e) {
                e.printStackTrace();
            }
            return s;
        }

        private <T> T getRequest(Class<T> clazz, String data) {
            return new Gson().fromJson(data, clazz);
        }

        private NotificationRequest getNotification(String data) {
            return new Gson().fromJson(data, NotificationRequest.class);
        }
    }

    @Override
    public void onPreparedAudio(String audioName, int duration) {

    }

    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onPaused() {
        if (svc.getUser().isOrganizer()) {
            Track track = MapperUtils.from(mPlayer.getCurrentAudio(), Track.TRACK_STATE_PAUSED);
            svc.getParty().getPlaylist().setCurrentTrack(track);
            NotifyEvent event = new NotifyEvent(EventService.EVENT_CURRENT_TRACK, svc.getParty().getPlaylist(), track);
            svc.notifyMembers(event);
        }
    }

    @Override
    public void onContinueAudio() {

    }

    @Override
    public void onPlaying() {
        if (svc.getUser().isOrganizer()) {
            Track track = MapperUtils.from(mPlayer.getCurrentAudio(), Track.TRACK_STATE_PLAYING);
            svc.getParty().getPlaylist().setCurrentTrack(track);
            NotifyEvent event = new NotifyEvent(EventService.EVENT_CURRENT_TRACK, svc.getParty().getPlaylist(), track);
            svc.notifyMembers(event);
        }
    }

    @Override
    public void onTimeChanged(long currentTime) {
        Track currentTrack = svc.getParty().getPlaylist().getCurrentTrack();
        if (currentTrack != null)
            currentTrack.setSeek(currentTime);
    }

    @Override
    public void updateTitle(String title) {

    }

    public void stopServer() {
        if (mServer != null) mServer.stop();
    }

    public void initMemberPlayer() {
        mPlayer = JcAudioPlayer.getInstance();
        mPlayer.registerServiceListener(this);
        updateMediaPlayerList(svc.getParty().getPlaylist());
        Track currentTrack = svc.getParty().getPlaylist().getCurrentTrack();
        if (currentTrack != null && currentTrack.isPlaying()) startPlaying(currentTrack);
    }

    public void initOrganizerPlayer() {
        mPlayer = JcAudioPlayer.getInstance();
        mPlayer.registerServiceListener(this);
        updateMediaPlayerList(svc.getParty().getPlaylist());
    }

    public void stopPlaying() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                mPlayer.reset(); // Reset player
                mPlayer.setPlaylist(MapperUtils.jcAudiosFrom(Track.generateMyPlaylistTracks())); // Set to default playlist
            }
        });
    }

    public void updateMediaPlayerList(final Playlist playlist) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                mPlayer.setPlaylist(MapperUtils.jcAudiosFrom(playlist.getTrackList()));
            }
        });
    }


    private void pausePlaying() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                mPlayer.pauseAudio();
            }
        });
    }

    private void startPlaying(final Track track) {
        if (track == null) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                mPlayer.playAudioHack(MapperUtils.from(track));
            }
        });
    }

    private void syncTrack(final int seekTo) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Log.d(TAG, "Sync to " + seekTo);
                if (svc.getParty() == null) return;
                if (svc.getParty().getPlaylist().getCurrentTrack() == null) return;
                Track currentTrack = svc.getParty().getPlaylist().getCurrentTrack();
                Log.d(TAG, "Current seek " + currentTrack.getSeek());
                int calculated = Math.abs((int) currentTrack.getSeek() - seekTo);
                Log.d(TAG, "Calculated seek " + calculated);
                if (calculated > 2500) mPlayer.seekTo(seekTo + 1000);
            }
        });
    }

    private void makeToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(svc, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
