package com.tinglabs.silent.party.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.api.PartyOrganizerAPI;
import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.util.EventManager;
import com.tinglabs.silent.party.util.EventUtils;


/**
 * Created by Talal on 11/27/2016.
 */

public class ControllerActivity extends BaseActivity implements UserAPI, PartyOrganizerAPI, PartyMemberAPI, ViewController {

    private static final String TAG = "ControllerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        registerEventReceiver();
        loadUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterEventReceiver();
    }

    @Override
    public void createUser(User user) {
        delegate(new Intent(ACTION_CREATE_USER).putExtra("user", user));
    }

    @Override
    public void loadUser() {
        delegate(new Intent(ACTION_LOAD_USER));
    }

    @Override
    public void findParties() {
        delegate(new Intent(ACTION_FIND_PARTIES));
    }

    @Override
    public void joinParty(Party party) {
        delegate(new Intent(ACTION_JOIN_PARTY).putExtra("party", party));
    }

    @Override
    public void createParty(Party party) {
        delegate(new Intent(ACTION_CREATE_PARTY).putExtra("party", party));
    }

    @Override
    public void userStatus() {
        delegate(new Intent(ACTION_USER_STATUS));
    }

    @Override
    public void quitParty() {
        delegate(new Intent(ACTION_USER_QUIT_PARTY));
    }

    @Override
    public void currentParty() {
        delegate(new Intent(ACTION_CURRENT_PARTY));
    }

    @Override
    public void listMembers() {
        delegate(new Intent(ACTION_LIST_MEMBERS));
    }

    @Override
    public void updateUser(User user) {
        delegate(new Intent(ACTION_UPDATE_USER).putExtra("user", user));
    }

    @Override
    public void kickMember(User user) {
        delegate(new Intent(ACTION_KICK_MEMBER).putExtra("member", user));
    }

    @Override
    public void closeParty() {
        delegate(new Intent(ACTION_CLOSE_PARTY));
    }

    @Override
    public void syncPlaylist() {
        delegate(new Intent(ACTION_SYNC_PLAYLIST));
    }

    @Override
    public void addTrack(Track track) {
        delegate(new Intent(ACTION_ADD_TRACK).putExtra("track", track));
    }

    @Override
    public void voteTrack(Track track, int vote) {
        delegate(new Intent(ACTION_VOTE_TRACK).putExtra("track", track).putExtra("vote", vote));
    }

    @Override
    public void listTracks() {
        delegate(new Intent(ACTION_LIST_TRACKS));
    }

    @Override
    public void updateMember(User member) {
        delegate(new Intent(ACTION_UPDATE_MEMBER).putExtra("member", member));
    }

    @Override
    public void leaveParty() {
        delegate(new Intent(ACTION_LEAVE_PARTY));
    }

    @Override
    public void startLoader() {
        super.showProgressBar();
    }

    @Override
    public void startLoader(String loadingText) {
        super.showProgressBar(loadingText);
    }

    @Override
    public void stopLoader() {
        super.hideProgressBar();
    }

    @Override
    public void showToast(String text) {
        super.createToast(text);
    }

    @Override
    public void showLoadingToast(String text) {
        super.createLoadingToast(text);
    }

    @Override
    public void dismissLoadingToast() {
        super.closeToast();
    }

    @Override
    public void showError(String text) {
        super.showErrorToast(text);
    }

    @Override
    public void navigateTo(int id) {
        super.gotoFragment(id);
    }

    @Override
    public void setPartyTitle(String text) {
        super.getSupportActionBar().setTitle(text);
    }

    @Override
    public void setHeaderTitle(String text) {
        super.setNavHeaderTitle(text);
    }

    @Override
    public void setHeaderStatus(String text) {
        super.setNavHeaderSubtitle(text);
    }

    @Override
    public void setHeaderImage(String path) {
        super.setNavHeaderImage(path);
    }

    @Override
    public void showPlayer() {
        super.showPlayerView();
    }

    @Override
    public void hidePlayer() {
        super.hidePlayerView();
    }

    @Override
    public void playTrack(Track track) {
        super.play(track);
    }

    @Override
    public void playTrack(Track track, int atTime) {
        super.play(track, atTime);
    }

    @Override
    public void pauseTrack() {
        super.pause();
    }

    @Override
    public void setPlaylist(Playlist playlist) {
        super.setPlaylist(playlist);
    }

    private void registerEventReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, EventUtils.createIntentFilter(Config.MOCK_MODE));
    }

    private void unregisterEventReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventManager.process(intent);
        }
    };
}
