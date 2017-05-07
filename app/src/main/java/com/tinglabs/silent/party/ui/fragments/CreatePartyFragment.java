package com.tinglabs.silent.party.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.event.CreatePartyEventListener;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.util.EventManager;

/**
 * Created by Talal on 11/27/2016.
 */

public class CreatePartyFragment extends Fragment implements CreatePartyEventListener {

    public static final String TAG = "CreatePartyFragment";

    public CreatePartyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_party, container, false);
        ButterKnife.bind(this, view);

        // Don't need player here
        ((ViewController) getActivity()).hidePlayer();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventManager.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventManager.unregister(this);
    }

    @Override
    public void partyCreated(Party party) {
        ((ViewController) getActivity()).stopLoader();
        ((ViewController) getActivity()).navigateTo(R.id.nav_current_party);
    }

    @Override
    public void onComplete(String action) {
    }

    @Override
    public void onError(String action, String reason) {
        Log.d(TAG, action + " : " + reason);
        ((ViewController) getActivity()).stopLoader();
        ((ViewController) getActivity()).showError(reason);
    }
}
