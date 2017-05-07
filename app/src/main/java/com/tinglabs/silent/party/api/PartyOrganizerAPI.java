package com.tinglabs.silent.party.api;

import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 11/26/2016.
 */

public interface PartyOrganizerAPI {

//  kick party members out of the party event
//  close the party event

    // @formatter:off
    String ACTION_CLOSE_PARTY       = "ACTION_CLOSE_PARTY";
    String ACTION_KICK_MEMBER       = "ACTION_KICK_MEMBER";
    // @formatter:on

    void kickMember(User user);

    void closeParty();
}
