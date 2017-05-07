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
import android.widget.EditText;
import android.widget.TextView;

import com.jlubecki.soundcloud.webapi.android.SoundCloudAPI;
import com.jlubecki.soundcloud.webapi.android.SoundCloudService;
import com.jlubecki.soundcloud.webapi.android.models.Track;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tinglabs.silent.party.api.PartyMemberAPI;
import com.tinglabs.silent.party.conf.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.player.JcAudio;
import com.tinglabs.silent.party.player.JcPlayerService;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.ui.items.SharedTrackItem;
import com.tinglabs.silent.party.util.MapperUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Talal on 11/27/2016.
 */

public class FindTrackFragment extends Fragment implements JcPlayerService.OnInvalidPathListener {

    public static final String TAG = "FindTrackFragment";

    private FastItemAdapter<SharedTrackItem> mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.search_query)
    EditText mQuery;

    @BindView(R.id.search_list_empty)
    TextView mNoTracks;

    private SoundCloudAPI api = new SoundCloudAPI(Config.SOUND_CLOUD_ID);
    private SoundCloudService sc = api.getService();

    public FindTrackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mContentView = inflater.inflate(R.layout.fragment_find_track, container, false);
        ButterKnife.bind(this, mContentView);

        // Set adapter
        mAdapter = new FastItemAdapter();
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
                showMenu(v, position, item);
            }
        });

        // Set recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        ((ViewController) getActivity()).showPlayer();
        searchTrack();

        return mContentView;
    }

    @OnClick(R.id.search_btn)
    public void searchTrack() {
        String query = mQuery.getText().toString();
        sc.searchTracks(query).enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                mAdapter.clear();
                mAdapter.add(MapperUtils.from(response.body()));
                if (mAdapter.getAdapterItemCount() > 0)
                    mNoTracks.setVisibility(View.GONE);
                else
                    mNoTracks.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });

    }


    @Override
    public void onPathError(JcAudio jcAudio) {
        Log.d(TAG, jcAudio.getPath());
    }

    private void showMenu(View view, final int position, final SharedTrackItem item) {
        PopupMenu menu = new PopupMenu(this.getContext(), view);
        menu.inflate(R.menu.shared_track_menu);
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_add_to_playlist:
                        item.addToMyPlaylist();
                        ((ViewController) getActivity()).showToast("Track added!");
                        return true;
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
