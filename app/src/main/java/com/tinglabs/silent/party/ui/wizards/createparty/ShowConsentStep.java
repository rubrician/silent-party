package com.tinglabs.silent.party.ui.wizards.createparty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tinglabs.silent.party.api.UserAPI;

import org.codepond.wizardroid.WizardStep;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import com.tinglabs.silent.party.R;

public class ShowConsentStep extends WizardStep {

    public static final String TAG = "ShowConsentStep";

    public ShowConsentStep() {
    }

    //Set your layout here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.step_0_create_party, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnCheckedChanged(R.id.agree_btn)
    public void agree(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            ((UserAPI) getActivity()).quitParty();
            notifyCompleted();
        }
    }
}
