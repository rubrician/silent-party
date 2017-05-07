package com.tinglabs.silent.party.model.comm;

import com.android.volley.Request;
import com.android.volley.Response;
import com.tinglabs.silent.party.api.PartyMemberAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Talal on 1/13/2017.
 */

public class SyncPlaylistRequest extends AbstractRequest {

    private Map<String, String> headers = new HashMap<String, String>() {{
        put("action", PartyMemberAPI.ACTION_SYNC_PLAYLIST);
    }};

    public SyncPlaylistRequest(String url) {
        super(url);
    }

    @Override
    public <T> Request<T> make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        return new GenericRequest<T>(Request.Method.POST, url, clazz, this, listener, errorListener, headers);
    }

    @Override
    public String toString() {
        return "SyncPlaylistRequest{" +
                "headers=" + headers +
                "} " + super.toString();
    }
}
