package com.tinglabs.silent.party.event;

import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 11/26/2016.
 */

public interface UserEventListener extends EventListener {

    void userCreated(User user);

    void userUpdated(User user);

    void userLoaded(User user);
}
