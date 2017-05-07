package com.tinglabs.silent.party.ui.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tinglabs.silent.party.api.PartyMemberAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.event.StatusEventListener;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.ui.adapters.ViewPagerAdapter;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.ui.tabs.currentparty.CurrentPartyMembersTab;
import com.tinglabs.silent.party.ui.tabs.currentparty.CurrentPartyPlaylistTab;
import com.tinglabs.silent.party.util.EventManager;


/**
 * Created by Talal on 11/27/2016.
 */

public class CurrentPartyFragment extends Fragment implements StatusEventListener {

    public static final String TAG = "CurrentPartyFragment";

    private String mRole;

    @BindView(R.id.pager_view)
    ViewPager mViewPager;

    @BindView(R.id.tabs_view)
    TabLayout mTabView;

    @BindView(R.id.in_party_view)
    LinearLayout inPartyView;

    @BindView(R.id.join_party_view)
    LinearLayout joinPartyView;

    public CurrentPartyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_current_party_bar_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_party, container, false);
        ButterKnife.bind(this, view);

        inPartyView.setVisibility(View.GONE);
        joinPartyView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventManager.register(this);

        // Retrieve status
        ((UserAPI) getActivity()).userStatus();
        ((ViewController) getActivity()).startLoader();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventManager.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_quit_party:
                if (User.Role.ORGANIZER.equals(mRole) || User.Role.MEMBER.equals(mRole)) {
                    ((UserAPI) getActivity()).quitParty();
                    ((ViewController) getActivity()).startLoader("Quiting...");
                } else {
                    ((ViewController) getActivity()).showError("Not in a party!");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void partyClosed(String party, String reason) {
        ((ViewController) getActivity()).showToast("Party closed by organizer!");
        showJoinView();
    }

    @Override
    public void partyServiceLost(String party) {

    }

    @Override
    public void userRole(String role) {
        Log.d(TAG, "User role: " + role);
        mRole = role;
        if (role == null || User.Role.NONE.equals(role)) {
            showJoinView();
        } else {
            // Retrieve party
            ((PartyMemberAPI) getActivity()).currentParty();
            ((ViewController) getActivity()).startLoader();
        }
    }

    @Override
    public void currentParty(Party party) {
        ((ViewController) getActivity()).setPartyTitle(party.getName());
        showPartyView(mRole, party.getPlaylist(), party.getMembersList(), party.getOrganizer());
    }

    @Override
    public void onComplete(String action) {
        ((ViewController) getActivity()).stopLoader();
    }

    @Override
    public void onError(String action, String reason) {

    }

    private void showPartyView(String role, Playlist playlist, List<User> members, User organizer) {
        ((ViewController) getActivity()).showPlayer();
        inPartyView.setVisibility(View.VISIBLE);

        CurrentPartyPlaylistTab currentPartyPlaylistTab = new CurrentPartyPlaylistTab();
        Bundle argsPlaylist = new Bundle();
        argsPlaylist.putString("role", role);
        argsPlaylist.putSerializable("playlist", playlist);
        currentPartyPlaylistTab.setArguments(argsPlaylist);

        CurrentPartyMembersTab currentPartyMembersTab = new CurrentPartyMembersTab();
        Bundle argsMembers = new Bundle();
        argsMembers.putString("role", role);
        argsMembers.putSerializable("organizer", organizer);
        argsMembers.putSerializable("members", (ArrayList<User>) members);
        currentPartyMembersTab.setArguments(argsMembers);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(currentPartyPlaylistTab, "Playlist");
        adapter.addFragment(currentPartyMembersTab, "Members");
        //adapter.addFragment(new CurrentPartyRequestsTab(), "Requests");

        mViewPager.setAdapter(adapter);
        mTabView.setupWithViewPager(mViewPager);
    }

    private void showJoinView() {
        ((ViewController) getActivity()).hidePlayer();
        inPartyView.setVisibility(View.GONE);
        joinPartyView.setVisibility(View.VISIBLE);

        Button createBtn = (Button) joinPartyView.findViewById(R.id.create_party_btn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewController) getActivity()).navigateTo(R.id.nav_create_party);
            }
        });

        Button joinBtn = (Button) joinPartyView.findViewById(R.id.join_party_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewController) getActivity()).navigateTo(R.id.nav_find_party);
            }
        });
    }
}
