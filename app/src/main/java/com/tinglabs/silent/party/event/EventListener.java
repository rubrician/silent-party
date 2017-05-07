package com.tinglabs.silent.party.event;

/**
 * Created by Talal on 1/12/2017.
 */

public interface EventListener {

    void onComplete(String action);

    void onError(String action, String reason);
}
