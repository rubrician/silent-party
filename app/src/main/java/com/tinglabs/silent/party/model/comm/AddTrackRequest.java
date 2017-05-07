package com.tinglabs.silent.party.model.comm;

import com.android.volley.Request;
import com.android.volley.Response;
import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.model.User;

import java.util.HashMap;
import java.util.Map;

import com.tinglabs.silent.party.model.Track;

/**
 * Created by Talal on 1/13/2017.
 */

public class AddTrackRequest extends AbstractRequest {

    private User user;
    private Track track;
    private Map<String, String> headers = new HashMap<String, String>() {{
        put("action", PartyMemberAPI.ACTION_ADD_TRACK);
    }};

    public AddTrackRequest(String url, User user, Track track) {
        super(url);
        this.track = track;
        this.user = user;
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

    @Override
    public <T> Request<T> make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        return new GenericRequest<T>(com.android.volley.Request.Method.POST, url, clazz, this, listener, errorListener, headers);
    }

    @Override
    public String toString() {
        return "AddTrackRequest{" +
                "user=" + user +
                ", track=" + track +
                ", headers=" + headers +
                "} " + super.toString();
    }
}
