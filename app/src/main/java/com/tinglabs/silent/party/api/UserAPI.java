package com.tinglabs.silent.party.api;

import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 11/26/2016.
 */

public interface UserAPI {

//  create a party event (> become organizer)
//  search for party events
//  join a party event
//  leave a party event
//  see other party members
//  each user needs a profile (name, picture,…<be creative>)

    // @formatter:off
    String ACTION_CREATE_USER           = "ACTION_CREATE_USER";
    String ACTION_LOAD_USER             = "ACTION_LOAD_USER";
    String ACTION_UPDATE_USER           = "ACTION_UPDATE_USER";
    String ACTION_FIND_PARTIES          = "ACTION_FIND_PARTIES";
    String ACTION_JOIN_PARTY            = "ACTION_JOIN_PARTY";
    String ACTION_CREATE_PARTY          = "ACTION_CREATE_PARTY";
    String ACTION_USER_STATUS           = "ACTION_USER_STATUS";
    String ACTION_USER_QUIT_PARTY       = "ACTION_USER_QUIT_PARTY";
    // @formatter:on

    void createUser(User user);

    void loadUser();

    void updateUser(User user);

    void findParties();

    void joinParty(Party party);

    void createParty(Party party);

    void userStatus();

    void quitParty();
}
