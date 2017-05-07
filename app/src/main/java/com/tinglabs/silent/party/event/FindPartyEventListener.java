package com.tinglabs.silent.party.event;

import com.tinglabs.silent.party.model.Party;

import java.util.List;

/**
 * Created by Talal on 1/8/2017.
 */

public interface FindPartyEventListener extends EventListener {

    void partiesFound(List<Party> partyList);

    void partyFound(Party party);
}
