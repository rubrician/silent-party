package com.tinglabs.silent.party.event;

import com.tinglabs.silent.party.model.Party;

/**
 * Created by Talal on 1/8/2017.
 */

public interface StatusEventListener extends EventListener {

    void partyClosed(String party, String reason);

    void partyServiceLost(String party);

    void userRole(String role);

    void currentParty(Party party);
}
