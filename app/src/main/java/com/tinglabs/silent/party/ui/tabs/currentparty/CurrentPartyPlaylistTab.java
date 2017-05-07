package com.tinglabs.silent.party.ui.tabs.currentparty;


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

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tinglabs.silent.party.api.PartyMemberAPI;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.event.PartyPlaylistEventListener;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.ui.items.SharedTrackItem;
import com.tinglabs.silent.party.util.EventManager;


/**
 * Created by Talal on 11/27/2016.
 */

public class CurrentPartyPlaylistTab extends Fragment implements PartyPlaylistEventListener {

    public static final String TAG = "CurrentPartyPlaylistTab";

    private Playlist mPlaylist;
    private FastItemAdapter<SharedTrackItem> mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public CurrentPartyPlaylistTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mPlaylist = (Playlist) getArguments().getSerializable("playlist");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_party_playlist_tab, container, false);
        ButterKnife.bind(this, view);

        // Set adapter
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<SharedTrackItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SharedTrackItem> adapter, SharedTrackItem item, int position) {
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
                Log.d(TAG, item.getTrack().toString());
                ((PartyMemberAPI) getActivity()).voteTrack(item.getTrack(), 1);
            }
        });

        // Set recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPlaylist != null) addAll(mPlaylist.getTrackList());
        EventManager.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventManager.unregister(this);
    }

    @Override
    public void trackAdded(Track track) {
        add(track);
    }

    @Override
    public void trackVoted(Track track) {
        update(track);
        sortPlaylist();
    }

    @Override
    public void currentTrack(Track track) {
        Log.d(TAG, "Current track " + track);
    }

    @Override
    public void trackList(List<Track> tracks) {
        addAll(tracks);
    }

    @Override
    public void syncPlaylist(Playlist playlist) {
        addAll(playlist.getTrackList());
    }

    @Override
    public void onComplete(String action) {
        ((ViewController) getActivity()).stopLoader();
    }

    @Override
    public void onError(String action, String reason) {
        ((ViewController) getActivity()).showError(reason);
    }

    private void showMenu(View view, final int position, final SharedTrackItem item) {
        PopupMenu menu = new PopupMenu(this.getContext(), view);
        menu.inflate(R.menu.fragment_current_party_playlist_item_menu);
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_remove_from_party_playlist:
                        mAdapter.remove(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void addAll(List<Track> tracks) {
        if (tracks == null) return;
        mAdapter.clear();
        for (Track track : tracks) {
            mAdapter.add(new SharedTrackItem(track, SharedTrackItem.Mode.VOTE));
        }
    }

    private void add(Track track) {
        if (track == null) return;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getAdapterItem(i).getTrack().getTrackId().equals(track.getTrackId())) {
                return;
            }
        }
        mAdapter.add(new SharedTrackItem(track, SharedTrackItem.Mode.VOTE));
    }

    private void remove(final Track track) {
        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<SharedTrackItem>() {
            @Override
            public boolean filter(SharedTrackItem item, CharSequence constraint) {
                return item.getTrack().getTrackId().equals(String.valueOf(constraint));
            }
        });
        mAdapter.filter(track.getTrackId());
    }

    private void update(Track track) {
        if (track == null) return;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getAdapterItem(i).getTrack().getTrackId().equals(track.getTrackId())) {
                mAdapter.getAdapterItem(i).setTrack(track);
                mAdapter.notifyAdapterItemChanged(i);
                return;
            }
        }
    }

    private void sortPlaylist() {
        Collections.sort(mAdapter.getAdapterItems(), new Comparator<SharedTrackItem>() {
            @Override
            public int compare(SharedTrackItem lhs, SharedTrackItem rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getTrack().getVotes() > rhs.getTrack().getVotes() ? -1 :
                        (lhs.getTrack().getVotes() < rhs.getTrack().getVotes()) ? 1 : 0;
            }
        });
        mAdapter.notifyAdapterDataSetChanged();
    }
}
