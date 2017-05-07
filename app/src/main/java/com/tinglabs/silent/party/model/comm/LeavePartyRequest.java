package com.tinglabs.silent.party.model.comm;

import com.android.volley.Request;
import com.android.volley.Response;
import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Talal on 1/2/2017.
 */

public class LeavePartyRequest extends AbstractRequest {

    private User user;
    private Map<String, String> headers = new HashMap<String, String>() {{
        put("action", PartyMemberAPI.ACTION_LEAVE_PARTY);
    }};

    public LeavePartyRequest(String url, User user) {
        super(url);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public <T> Request<T> make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        return new GenericRequest<T>(com.android.volley.Request.Method.POST, url, clazz, this, listener, errorListener, headers);
    }

    @Override
    public String toString() {
        return "LeavePartyRequest{" +
                "user=" + user +
                ", headers=" + headers +
                "} " + super.toString();
    }
}
