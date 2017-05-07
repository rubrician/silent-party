package com.tinglabs.silent.party.ui.wizards.createparty;

import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.User;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;
import org.codepond.wizardroid.persistence.ContextVariable;

import java.util.ArrayList;

import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.ui.interfaces.ViewController;

public class CreatePartyWizard extends BasicWizardLayout {
    public static final String TAG = "CreatePartyWizard";

    @ContextVariable
    private Party party;

    public CreatePartyWizard() {
        super();
    }

    @Override
    public WizardFlow onSetup() {
        // Init defaults
        party = new Party();
        party.setPlaylist(new Playlist(Config.DEFAULT_PARTY_PLAYLIST, new ArrayList<Track>()));
        party.setMembersList(new ArrayList<User>());
        party.setStatus(Party.Status.OPEN);

        return new WizardFlow.Builder()
                .addStep(ShowConsentStep.class, true)
                .addStep(SetNameStep.class)
                .addStep(SetPlaylistStep.class, true)
                .addStep(ShowSummaryStep.class)
                .create();
    }

    @Override
    public void onWizardComplete() {
        super.onWizardComplete();

        ((ViewController) getActivity()).startLoader();
        ((UserAPI) getActivity()).createParty(party);
        ((ViewController) getActivity()).setPlaylist(party.getPlaylist());
    }
}
