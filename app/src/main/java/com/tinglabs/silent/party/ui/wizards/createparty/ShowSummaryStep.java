package com.tinglabs.silent.party.ui.wizards.createparty;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.ui.items.SharedTrackItem;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

public class ShowSummaryStep extends WizardStep {

    public static final String TAG = "ShowSummaryStep";

    private FastItemAdapter<SharedTrackItem> mAdapter;

    @ContextVariable
    private Party party;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.party_name)
    TextView mPartyName;

    public ShowSummaryStep() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.step_3_create_party, container, false);
        ButterKnife.bind(this, v);

        // Set party details
        mPartyName.setText(party.getName());

        // set adapter
        mAdapter = new FastItemAdapter<>();

        // Set recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        initPlaylist();

        return v;
    }

    private void initPlaylist() {
        for (Track track : party.getPlaylist().getTrackList()) {
            mAdapter.add(new SharedTrackItem(track, SharedTrackItem.Mode.NONE));
        }
    }
}
