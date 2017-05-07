package com.tinglabs.silent.party.model.comm;

import com.android.volley.Request;
import com.android.volley.Response;
import com.tinglabs.silent.party.api.PartyMemberAPI;

import java.util.HashMap;
import java.util.Map;

import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 1/2/2017.
 */

public class VoteTrackRequest extends AbstractRequest {

    private User user;
    private Track track;
    private int vote; // 1 for up -1 for down;

    private Map<String, String> headers = new HashMap<String, String>() {{
        put("action", PartyMemberAPI.ACTION_VOTE_TRACK);
    }};


    public VoteTrackRequest(String url, User user, Track track, int vote) {
        super(url);
        this.user = user;
        this.track = track;
        this.vote = vote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    @Override
    public <T> Request<T> make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        return new GenericRequest<T>(Request.Method.POST, url, clazz, this, listener, errorListener, headers);
    }

    @Override
    public String toString() {
        return "VoteTrackRequest{" +
                "user=" + user +
                ", track=" + track +
                ", vote=" + vote +
                ", headers=" + headers +
                "} " + super.toString();
    }
}
