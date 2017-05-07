package com.tinglabs.silent.party.model.comm;

import com.android.volley.Request;
import com.android.volley.Response;

/**
 * Created by Talal on 1/14/2017.
 */

public abstract class AbstractRequest {
    protected String url;

    AbstractRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public abstract <T> Request<T> make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener);
}