package com.tinglabs.silent.party.util;

import android.content.Intent;
import android.content.IntentFilter;

import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.service.EventService;
import com.tinglabs.silent.party.service.MockService;

/**
 * Created by Talal on 12/25/2016.
 */

public class EventUtils {

    public static String resolveServerUrl(String hostIp) {
        return "http://" + hostIp + ":" + Config.DEFAULT_SERVER_PORT;
    }

    public static IntentFilter createIntentFilter(boolean mockModeOn) {
        return mockModeOn ? createMockFilters() : createEventFilters();
    }

    public static String resolveAction(boolean isMockMode, String action) {
        return isMockMode ? String.format(MockService.ACTION_FORMAT, action) : String.format(EventService.ACTION_FORMAT, action);
    }

    public static void resolveMockAction(Intent intent) {
        intent.setAction(String.format(MockService.ACTION_FORMAT, intent.getAction()));
    }

    public static void resolveEventAction(Intent intent) {
        intent.setAction(String.format(EventService.ACTION_FORMAT, intent.getAction()));
    }

    private static IntentFilter createMockFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MockService.EVENT_PARTY_CREATED);
        filter.addAction(MockService.EVENT_PARTY_CLOSED);
        filter.addAction(MockService.EVENT_PARTIES_FOUND);
        filter.addAction(MockService.EVENT_NEW_PARTY_FOUND);
        filter.addAction(MockService.EVENT_CURRENT_PARTY);
        filter.addAction(MockService.EVENT_TRACK_ADDED);
        filter.addAction(MockService.EVENT_TRACK_VOTED);
        filter.addAction(MockService.EVENT_TRACK_PLAYING);
        filter.addAction(MockService.ACTION_SYNC_PLAYLIST);
        filter.addAction(MockService.EVENT_MEMBERS_LIST);
        filter.addAction(MockService.EVENT_MEMBER_JOINED);
        filter.addAction(MockService.EVENT_MEMBER_LEFT);
        filter.addAction(MockService.EVENT_MEMBER_UPDATED);
        filter.addAction(MockService.EVENT_MEMBER_KICKED);
        filter.addAction(MockService.EVENT_USER_ROLE);
        filter.addAction(MockService.EVENT_USER_LOADED);
        filter.addAction(MockService.EVENT_USER_CREATED);
        filter.addAction(MockService.EVENT_USER_UPDATED);
        filter.addAction(MockService.EVENT_ERROR);
        return filter;
    }

    private static IntentFilter createEventFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(EventService.EVENT_PARTY_CREATED);
        filter.addAction(EventService.EVENT_PARTY_CLOSED);
        filter.addAction(EventService.EVENT_PARTIES_FOUND);
        filter.addAction(EventService.EVENT_PARTY_SERVICE_LOST);
        filter.addAction(EventService.EVENT_NEW_PARTY_FOUND);
        filter.addAction(EventService.EVENT_CURRENT_PARTY);
        filter.addAction(EventService.EVENT_TRACK_ADDED);
        filter.addAction(EventService.EVENT_TRACK_VOTED);
        filter.addAction(EventService.EVENT_CURRENT_TRACK);
        filter.addAction(EventService.EVENT_SYNC_PLAYLIST);
        filter.addAction(EventService.EVENT_TRACKS_LIST);
        filter.addAction(EventService.EVENT_MEMBERS_LIST);
        filter.addAction(EventService.EVENT_MEMBER_JOINED);
        filter.addAction(EventService.EVENT_MEMBER_LEFT);
        filter.addAction(EventService.EVENT_MEMBER_UPDATED);
        filter.addAction(EventService.EVENT_MEMBER_KICKED);
        filter.addAction(EventService.EVENT_USER_ROLE);
        filter.addAction(EventService.EVENT_USER_LOADED);
        filter.addAction(EventService.EVENT_USER_CREATED);
        filter.addAction(EventService.EVENT_USER_UPDATED);
        filter.addAction(EventService.EVENT_ERROR);
        return filter;
    }
}
