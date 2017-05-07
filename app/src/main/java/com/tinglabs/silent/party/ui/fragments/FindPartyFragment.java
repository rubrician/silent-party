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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.event.FindPartyEventListener;
import com.tinglabs.silent.party.event.StatusEventListener;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.User;

import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.ui.items.FindPartyItem;
import com.tinglabs.silent.party.util.EventManager;

/**
 * Created by Talal on 11/27/2016.
 */

public class FindPartyFragment extends Fragment implements FindPartyEventListener, StatusEventListener {

    public static final String TAG = "FindPartyFragment";

    private FastItemAdapter<FindPartyItem> mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.party_list_empty)
    TextView mNoParties;

    public FindPartyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_find_party_bar_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_party, container, false);
        ButterKnife.bind(this, view);

        // Hide player
        ((ViewController) getActivity()).hidePlayer();

        // Set adapter
        mAdapter = new FastItemAdapter<>();

        mAdapter.withItemEvent(new ClickEventHook<FindPartyItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FindPartyItem.ViewHolder) {
                    return ClickListenerHelper.toList(((FindPartyItem.ViewHolder) viewHolder).menuBtn);
                }
                return super.onBindMany(viewHolder);
            }

            @Override
            public void onClick(View v, int position, FastAdapter<FindPartyItem> fastAdapter, FindPartyItem item) {
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

        // Find parties
        ((UserAPI) getActivity()).findParties();
        ((ViewController) getActivity()).startLoader("Finding parties...");
    }

    @Override
    public void onPause() {
        super.onPause();
        EventManager.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                ((UserAPI) getActivity()).findParties();
                ((ViewController) getActivity()).startLoader("Finding parties...");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void partiesFound(List<Party> partyList) {
        mAdapter.clear();
        addAll(partyList);
    }

    @Override
    public void partyFound(Party party) {
        add(party);
    }

    @Override
    public void partyClosed(String party, String reason) {
        remove(party);
    }

    @Override
    public void partyServiceLost(String party) {
        remove(party);
    }

    @Override
    public void userRole(String role) {
        if (User.Role.MEMBER.equals(role)) {
            ((ViewController) getActivity()).navigateTo(R.id.nav_current_party);
        }
    }

    @Override
    public void currentParty(Party party) {
    }

    @Override
    public void onComplete(String action) {
        if (mAdapter.getAdapterItemCount() > 0) {
            mNoParties.setVisibility(View.GONE);
            ((ViewController) getActivity()).stopLoader();
        } else {
            mNoParties.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(String action, String reason) {
        Log.d(TAG, action + " : " + reason);
        ((ViewController) getActivity()).stopLoader();
    }

    private void showMenu(View view, final int position, final FindPartyItem item) {
        PopupMenu menu = new PopupMenu(this.getContext(), view);
        menu.inflate(R.menu.fragment_find_party_item_menu);
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_join_party:
                        ((UserAPI) getActivity()).joinParty(item.getParty());
                        ((ViewController) getActivity()).startLoader("Joining party...");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void addAll(List<Party> partyList) {
        for (Party party : partyList) {
            mAdapter.add(new FindPartyItem(party));
        }
    }

    private void add(Party party) {
        ListIterator<FindPartyItem> itr = mAdapter.getAdapterItems().listIterator();
        while (itr.hasNext()) {
            if (itr.next().getParty().getHostIp().equals(party.getHostIp()))
                return;
        }
        mAdapter.add(new FindPartyItem(party));
    }

    private void remove(final String party) {
        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<FindPartyItem>() {
            @Override
            public boolean filter(FindPartyItem item, CharSequence constraint) {
                return item.getParty().getName().equals(String.valueOf(constraint));
            }
        });
        mAdapter.filter(party);
    }
}
