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

public class UpdateMemberRequest extends AbstractRequest {
    private User user;
    private Map<String, String> headers = new HashMap<String, String>() {{
        put("action", PartyMemberAPI.ACTION_UPDATE_MEMBER);
    }};

    public UpdateMemberRequest(String url, User user) {
        super(url);
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public <T> Request make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        return new GenericRequest<T>(com.android.volley.Request.Method.POST, url, clazz, this, listener, errorListener, headers);
    }

    @Override
    public String toString() {
        return "PartyInfoRequest{" +
                "url='" + url + '\'' +
                ", user=" + user +
                ", headers=" + headers +
                '}';
    }
}
