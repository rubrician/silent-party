package com.tinglabs.silent.party.event;

import com.tinglabs.silent.party.model.Party;

/**
 * Created by Talal on 1/8/2017.
 */

public interface CreatePartyEventListener extends EventListener {

    void partyCreated(Party party);
}
