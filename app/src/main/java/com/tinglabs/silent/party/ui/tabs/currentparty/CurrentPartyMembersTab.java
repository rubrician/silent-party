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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.api.PartyOrganizerAPI;
import com.tinglabs.silent.party.event.PartyMemberEventListener;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.ui.items.CurrentPartyMemberItem;
import com.tinglabs.silent.party.util.EventManager;


/**
 * Created by Talal on 11/27/2016.
 */

public class CurrentPartyMembersTab extends Fragment implements PartyMemberEventListener {

    public static final String TAG = "CurrentPartyMembersTab";

    private String mRole;
    private FastItemAdapter<CurrentPartyMemberItem> mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.members_empty)
    TextView mNoMembers;

    @BindView(R.id.organizer_nick)
    TextView mOrganizerNick;

    @BindView(R.id.current_party_organizer_item)
    LinearLayout mOrganizerItem;

    public CurrentPartyMembersTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) mRole = getArguments().getString("role");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_party_members_tab, container, false);
        ButterKnife.bind(this, view);

        // Set adapter
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<CurrentPartyMemberItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CurrentPartyMemberItem> adapter, CurrentPartyMemberItem item, int position) {
                return true;
            }
        });


        mAdapter.withItemEvent(new ClickEventHook<CurrentPartyMemberItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CurrentPartyMemberItem.ViewHolder) {
                    return ClickListenerHelper.toList(((CurrentPartyMemberItem.ViewHolder) viewHolder).menuBtn);
                }
                return super.onBindMany(viewHolder);
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CurrentPartyMemberItem> fastAdapter, CurrentPartyMemberItem item) {
                showMenu(v, position, item);
            }
        });
        // Set recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        // Add members and set organizer details
        if (getArguments() != null) {
            addAll((ArrayList<User>) getArguments().getSerializable("members"));
            if (User.Role.ORGANIZER.equals(mRole)) {
                mOrganizerItem.setVisibility(View.GONE);
            } else {
                mOrganizerNick.setText(((User) getArguments().getSerializable("organizer")).getUserName());
                mOrganizerItem.setVisibility(View.VISIBLE);
            }
        }

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
    public void memberList(List<User> users) {
        if (users == null) return;
        mAdapter.clear();
        addAll(users);
    }

    @Override
    public void memberJoined(User user) {
        add(user);
    }

    @Override
    public void memberKicked(User user) {
        Log.d(TAG, "Member kicked " + user);
        remove(user);
    }

    @Override
    public void memberLeft(User user) {
        Log.d(TAG, "Member left " + user);
        remove(user);
    }

    @Override
    public void memberUpdated(User user) {
        Log.d(TAG, "Member left " + user);
        update(user);
    }

    @Override
    public void onComplete(String action) {
        if (mAdapter.getAdapterItemCount() > 0)
            mNoMembers.setVisibility(View.GONE);
        else
            mNoMembers.setVisibility(View.VISIBLE);
        ((ViewController) getActivity()).stopLoader();
    }

    @Override
    public void onError(String action, String reason) {
        Log.d(TAG, action + " : " + reason);
    }


    private void showMenu(View view, final int position, final CurrentPartyMemberItem item) {
        PopupMenu menu = new PopupMenu(this.getContext(), view);
        menu.inflate(R.menu.fragment_current_party_members_item_menu);
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_kick_member:
                        ((PartyOrganizerAPI) getActivity()).kickMember(item.getUser());
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void addAll(List<User> users) {
        if (users == null) return;
        CurrentPartyMemberItem.Mode mode = User.Role.MEMBER.equals(mRole)
                ? CurrentPartyMemberItem.Mode.NONE
                : CurrentPartyMemberItem.Mode.MENU;
        for (User user : users) {
            mAdapter.add(new CurrentPartyMemberItem(user, mode));
        }
    }

    private void add(User user) {
        ListIterator<CurrentPartyMemberItem> itr = mAdapter.getAdapterItems().listIterator();
        while (itr.hasNext()) {
            if (itr.next().getUser().getUserIp().equals(user.getUserIp()))
                return;
        }
        CurrentPartyMemberItem.Mode mode = User.Role.MEMBER.equals(mRole)
                ? CurrentPartyMemberItem.Mode.NONE
                : CurrentPartyMemberItem.Mode.MENU;
        mAdapter.add(new CurrentPartyMemberItem(user, mode));
    }

    private void remove(User user) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getAdapterItem(i).getUser().getUserIp().equals(user.getUserIp())) {
                mAdapter.remove(i);
            }
        }
    }

    private void update(User user) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getAdapterItem(i).getUser().getUserIp().equals(user.getUserIp())) {
                mAdapter.getAdapterItem(i).setUser(user);
                mAdapter.notifyAdapterItemChanged(i);
                return;
            }
        }
    }
}
