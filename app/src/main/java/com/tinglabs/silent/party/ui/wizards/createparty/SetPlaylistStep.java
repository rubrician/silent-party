package com.tinglabs.silent.party.ui.wizards.createparty;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.ui.items.SharedTrackItem;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.model.Track;

public class SetPlaylistStep extends WizardStep {

    public static final String TAG = "SetPlaylistStep";

    @ContextVariable
    private Party party;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private FastItemAdapter<SharedTrackItem> mAdapter;

    public SetPlaylistStep() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.step_2_create_party, container, false);
        ButterKnife.bind(this, v);

        // Set adapter
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<SharedTrackItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SharedTrackItem> adapter, SharedTrackItem item, int position) {
                return true;
            }
        });

        mAdapter.withItemEvent(new ClickEventHook<SharedTrackItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof SharedTrackItem.ViewHolder) {
                    return ClickListenerHelper.toList(((SharedTrackItem.ViewHolder) viewHolder).imgBtn);
                }
                return super.onBindMany(viewHolder);
            }

            @Override
            public void onClick(View v, int position, FastAdapter<SharedTrackItem> fastAdapter, SharedTrackItem item) {
                item.addOrRemoveFromPartyPlaylist((ImageButton) v, party);
                notifyCompleted();
            }
        });

        // Set recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        initPlaylist();

        return v;
    }

    private void initPlaylist() {
        Iterator<Track> tracks = Track.getAll(Config.DEFAULT_MY_PLAYLIST);
        while (tracks.hasNext()) {
            mAdapter.add(new SharedTrackItem(tracks.next(), SharedTrackItem.Mode.SELECTION));
        }
    }
}
