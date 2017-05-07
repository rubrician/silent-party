package com.tinglabs.silent.party.ui.wizards.createparty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tinglabs.silent.party.model.Party;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

public class SetNameStep extends WizardStep {

    public static final String TAG = "SetNameStep";

    @ContextVariable
    private Party party;

    @BindView(R.id.party_name)
    EditText mPartyName;

    public SetNameStep() {
    }

    //Set your layout here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.step_1_create_party, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onExit(int exitCode) {
        switch (exitCode) {
            case WizardStep.EXIT_NEXT:
                bindDataFields();
                break;
            case WizardStep.EXIT_PREVIOUS:
                //Do nothing...
                break;
        }
    }

    private void bindDataFields() {
        party.setName(mPartyName.getText().toString());
    }
}
