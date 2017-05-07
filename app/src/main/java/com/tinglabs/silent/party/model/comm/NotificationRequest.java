package com.tinglabs.silent.party.model.comm;

import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Talal on 1/2/2017.
 */

public class NotificationRequest extends AbstractRequest {

    private NotifyEvent event;
    private Map<String, String> headers = new HashMap<>();

    public NotificationRequest(String url, NotifyEvent event) {
        super(url);
        this.event = event;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NotifyEvent getEvent() {
        return event;
    }

    public void setEvent(NotifyEvent event) {
        this.event = event;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public <T> Request make(Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this.headers.put("action", event.getName());
        return new GenericRequest<T>(Request.Method.POST, url, clazz, this, listener, errorListener, headers);
    }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "event=" + event +
                ", headers=" + headers +
                "} " + super.toString();
    }
}
