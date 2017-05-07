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
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tinglabs.silent.party.ui.items.CurrentPartyRequestItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.event.EventListener;
import com.tinglabs.silent.party.util.EventManager;


/**
 * Created by Talal on 11/27/2016.
 */

public class CurrentPartyRequestsTab extends Fragment implements EventListener {
    public static final String TAG = "CurrentPartyRequestsTab";

    private FastItemAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public CurrentPartyRequestsTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_party_requests_tab, container, false);
        ButterKnife.bind(this, view);

        // Set adapter
        mAdapter = new FastItemAdapter();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<CurrentPartyRequestItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CurrentPartyRequestItem> adapter, CurrentPartyRequestItem item, int position) {
                Log.d(TAG, item.getRequest().getUser().getName());
                return true;
            }
        });

        mAdapter.withItemEvent(new ClickEventHook<CurrentPartyRequestItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CurrentPartyRequestItem.ViewHolder) {
                    return ClickListenerHelper.toList(((CurrentPartyRequestItem.ViewHolder) viewHolder).menuBtn);
                }
                return super.onBindMany(viewHolder);
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CurrentPartyRequestItem> fastAdapter, CurrentPartyRequestItem item) {
                showMenu(v, position, item);
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
        EventManager.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventManager.unregister(this);
    }

    @Override
    public void onComplete(String action) {
        Log.d(TAG, action);
    }

    @Override
    public void onError(String action, String reason) {
        Log.d(TAG, action);
    }


    private void showMenu(View view, final int position, final CurrentPartyRequestItem item) {
        PopupMenu menu = new PopupMenu(this.getContext(), view);
        menu.inflate(R.menu.shared_track_menu);
        // Remove items we don't need
        menu.getMenu().removeItem(R.id.menu_add_to_party);
        menu.getMenu().removeItem(R.id.menu_remove_from_playlist);
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_add_to_playlist:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
