package com.tinglabs.silent.party.model.comm;

import com.google.gson.Gson;

/**
 * Created by Talal on 1/14/2017.
 */

public abstract class AbstractResponse {

    protected boolean success;

    public AbstractResponse(boolean success) {
        this.success = success;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}