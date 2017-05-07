package com.tinglabs.silent.party.event;

import com.tinglabs.silent.party.model.User;

import java.util.List;

/**
 * Created by Talal on 11/26/2016.
 */

public interface PartyMemberEventListener extends EventListener{

    void memberList(List<User> users);

    void memberJoined(User user);

    void memberKicked(User user);

    void memberLeft(User user);

    void memberUpdated(User user);
}
