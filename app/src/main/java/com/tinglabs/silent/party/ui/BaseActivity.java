package com.tinglabs.silent.party.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.event.StatusEventListener;
import com.tinglabs.silent.party.event.UserEventListener;
import com.tinglabs.silent.party.model.Party;
import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.player.JcPlayerView;
import com.tinglabs.silent.party.service.EventService;
import com.tinglabs.silent.party.service.MockService;
import com.tinglabs.silent.party.ui.fragments.CreatePartyFragment;
import com.tinglabs.silent.party.ui.fragments.CurrentPartyFragment;
import com.tinglabs.silent.party.ui.fragments.FindPartyFragment;
import com.tinglabs.silent.party.ui.fragments.FindTrackFragment;
import com.tinglabs.silent.party.ui.fragments.MyPlaylistFragment;
import com.tinglabs.silent.party.ui.fragments.MyProfileFragment;
import com.tinglabs.silent.party.ui.fragments.SettingsFragment;
import com.tinglabs.silent.party.util.EventUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.util.EventManager;
import com.tinglabs.silent.party.util.MapperUtils;

/**
 * Created by Talal on 11/27/2016.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UserEventListener, StatusEventListener {

    public static final String TAG = "BaseActivity";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigation;

    @BindView(R.id.jcp_view)
    JcPlayerView mPlayer;

    private TextView mHeaderTitle;
    private TextView mHeaderSubtitle;
    private CircleImageView mHeaderImage;

    private LinearLayout mProgressBar;
    private TextView mProgressBarText;

    private SuperActivityToast mToast;

    protected String mUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Init player
        mPlayer.initPlaylist(MapperUtils.jcAudiosFrom(Track.generateMyPlaylistTracks()));

        // Set ProgressBar
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        mProgressBarText = new TextView(this, null, android.R.attr.textAppearanceMedium);
        mProgressBarText.setGravity(Gravity.CENTER);
        mProgressBarText.setPadding(0, 0, 0, 70);
        mProgressBarText.setTextColor(getResources().getColor(R.color.white));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mProgressBar = new LinearLayout(this);
        mProgressBar.setOrientation(LinearLayout.VERTICAL);
        mProgressBar.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mProgressBar.addView(mProgressBarText);
        mProgressBar.addView(pb);
        mProgressBar.setAlpha(0.8f);

        ViewGroup layout = (ViewGroup) findViewById(R.id.content_frame);
        layout.addView(mProgressBar, params);

        View headerView = mNavigation.getHeaderView(0);
        mHeaderTitle = (TextView) headerView.findViewById(R.id.header_title);
        mHeaderSubtitle = (TextView) headerView.findViewById(R.id.header_subtitle);
        mHeaderImage = (CircleImageView) headerView.findViewById(R.id.header_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigation.setNavigationItemSelectedListener(this);

        gotoFragment(R.id.nav_current_party);

        hideProgressBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventManager.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventManager.unregister(this);
        mPlayer.createNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) mPlayer.kill();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        String tag = "";

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_current_party) {
            tag = CurrentPartyFragment.TAG;
            fragment = new CurrentPartyFragment();
        } else if (id == R.id.nav_create_party) {
            tag = CreatePartyFragment.TAG;
            fragment = new CreatePartyFragment();
            //clearBackStack();
        } else if (id == R.id.nav_find_party) {
            tag = FindPartyFragment.TAG;
            fragment = new FindPartyFragment();
        } else if (id == R.id.nav_my_playlist) {
            tag = MyPlaylistFragment.TAG;
            fragment = new MyPlaylistFragment();
        } else if (id == R.id.nav_find_track) {
            tag = FindTrackFragment.TAG;
            fragment = new FindTrackFragment();
        } else if (id == R.id.nav_my_profile) {
            tag = MyProfileFragment.TAG;
            fragment = new MyProfileFragment();
        } else if (id == R.id.nav_settings) {
            tag = SettingsFragment.TAG;
            fragment = new SettingsFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                //.addToBackStack(null) // Commented because of wizardroid has issue with backstack
                .commit();

        mDrawer.closeDrawer(GravityCompat.START);
        getSupportActionBar().setTitle(item.getTitle());

        hideProgressBar();

        return true;
    }

    protected void delegate(Intent intent) {
        if (Config.MOCK_MODE) {
            EventUtils.resolveMockAction(intent);
            delegateMockService(intent);
        } else {
            EventUtils.resolveEventAction(intent);
            delegateEventService(intent);
        }
    }

    protected void delegateEventService(Intent intent) {
        intent.setClass(this, EventService.class);
        startService(intent);
    }

    protected void delegateMockService(Intent intent) {
        intent.setClass(this, MockService.class);
        startService(intent);
    }

    protected void showProgressBar() {
        if (mProgressBarText != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void showProgressBar(String loadingText) {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
        if (mProgressBarText != null) mProgressBarText.setText(loadingText);
    }

    protected void hideProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        if (mProgressBarText != null) mProgressBarText.setText(null);
    }

    protected void createToast(String text) {
        new SuperActivityToast(this, Style.blueGrey(), Style.TYPE_STANDARD)
                .setDuration(Style.DURATION_MEDIUM)
                .setText(text)
                .show();
    }

    protected void createLoadingToast(String text) {
        mToast = new SuperActivityToast(this, Style.blue(), Style.TYPE_PROGRESS_BAR)
                .setProgressIndeterminate(true)
                .setIndeterminate(true);
        mToast.setText(text);
        mToast.show();
    }

    protected void closeToast() {
        if (mToast != null) mToast.dismiss();
    }

    protected void showErrorToast(String text) {
        if (mToast != null) mToast.dismiss();
        mToast = new SuperActivityToast(this, Style.blue(), Style.TYPE_STANDARD);
        mToast.setDuration(Style.DURATION_MEDIUM);
        mToast.setText(text);
        mToast.show();
    }

    protected void gotoFragment(int id) {
        MenuItem item = mNavigation.getMenu().findItem(id);
        hideProgressBar();
        onNavigationItemSelected(item.setChecked(true));
    }

    public void setNavHeaderTitle(String text) {
        mHeaderTitle.setText(text);
    }

    public void setNavHeaderSubtitle(String text) {
        mHeaderSubtitle.setText(text);
    }

    public void setNavHeaderImage(String path) {
        try {
            mHeaderImage.setImageURI(Uri.parse(path));
        } catch (Exception e) {
            //ignored
        }
    }

    protected void showPlayerView() {
        mPlayer.setVisibility(View.VISIBLE);
    }

    protected void hidePlayerView() {
        mPlayer.setVisibility(View.GONE);
    }

    protected void play(Track track) {
        if (mUserRole != null && !User.Role.MEMBER.equals(mUserRole)) {
            mPlayer.playAudio(MapperUtils.from(track));
        }
    }

    protected void play(Track track, int atTime) {
        mPlayer.playAudio(MapperUtils.from(track));
        mPlayer.onTimeChanged(track.getSeek());
    }

    protected void pause() {
        mPlayer.pause();
    }

    protected void setPlaylist(Playlist playlist) {
        Log.d(TAG, "Setting tracks: " + playlist.getTrackList());
        mPlayer.initPlaylist(MapperUtils.jcAudiosFrom(playlist.getTrackList()));
    }

    protected void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        // another way is:
        // getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void userCreated(User user) {
        setNavHeaderTitle(user.getName());
    }

    @Override
    public void userUpdated(User user) {
        setNavHeaderTitle(user.getName());
        if (user.getPicUrl() != null) mHeaderImage.setImageURI(Uri.parse(user.getPicUrl()));
    }

    @Override
    public void userLoaded(User user) {
        setNavHeaderTitle(user.getName());
        if (user.getPicUrl() != null) mHeaderImage.setImageURI(Uri.parse(user.getPicUrl()));
    }

    @Override
    public void partyClosed(String party, String reason) {

    }

    @Override
    public void partyServiceLost(String party) {

    }

    @Override
    public void userRole(String role) {
        mUserRole = role;
        String status;
        if (User.Role.MEMBER.equals(role)) {
            mPlayer.hideControls();
            status = "Partying!";
        } else {
            mPlayer.showControls();
            status = "Idle...";
        }
        setNavHeaderSubtitle(status);
    }

    @Override
    public void currentParty(Party party) {

    }

    @Override
    public void onComplete(String action) {

    }

    @Override
    public void onError(String action, String reason) {

    }
}
