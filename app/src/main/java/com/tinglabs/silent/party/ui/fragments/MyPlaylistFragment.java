package com.tinglabs.silent.party.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.player.JcPlayerService;
import com.tinglabs.silent.party.ui.items.SharedTrackItem;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.player.JcAudio;
import com.tinglabs.silent.party.ui.interfaces.ViewController;


/**
 * Created by Talal on 11/27/2016.
 */

public class MyPlaylistFragment extends Fragment implements JcPlayerService.OnInvalidPathListener {

    public static final String TAG = "MyPlaylistFragment";

    private FastItemAdapter<SharedTrackItem> mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.track_list_empty)
    TextView mNoTracks;

    public MyPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mContentView = inflater.inflate(R.layout.fragment_my_playlist, container, false);
        ButterKnife.bind(this, mContentView);

        // Set adapter
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<SharedTrackItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SharedTrackItem> adapter, SharedTrackItem item, int position) {
                Log.d(TAG, item.getTrack().getStreamUrl());
                ((ViewController) getActivity()).playTrack(item.getTrack());
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
                showMenu(v, position, item);
            }
        });

        // Set recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        ((ViewController) getActivity()).showPlayer();
        initPlaylist();

        return mContentView;
    }

    @Override
    public void onPathError(JcAudio jcAudio) {
        Log.d(TAG, jcAudio.getPath());
    }

    private void initPlaylist() {
        Iterator<Track> tracks = Track.getAll(Config.DEFAULT_MY_PLAYLIST);
        while (tracks.hasNext()) {
            mAdapter.add(new SharedTrackItem(tracks.next()));
        }
        if (mAdapter.getAdapterItemCount() > 0)
            mNoTracks.setVisibility(View.GONE);
        else
            mNoTracks.setVisibility(View.VISIBLE);

    }

    private void showMenu(View view, final int position, final SharedTrackItem item) {
        PopupMenu menu = new PopupMenu(this.getContext(), view);
        menu.inflate(R.menu.shared_track_menu);
        // Hide items we don't want for this fragment
        menu.getMenu().removeItem(R.id.menu_add_to_playlist);
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_remove_from_playlist:
                        item.removeFromMyPlaylist();
                        mAdapter.remove(position);
                        ((ViewController) getActivity()).showToast("Track removed!");
                        return true;
                    case R.id.menu_add_to_party:
                        ((PartyMemberAPI) getActivity()).addTrack(item.getTrack());
                        ((ViewController) getActivity()).showToast("Sending to party!");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
