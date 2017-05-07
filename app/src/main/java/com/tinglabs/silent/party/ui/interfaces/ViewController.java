package com.tinglabs.silent.party.ui.interfaces;

import com.tinglabs.silent.party.model.Playlist;
import com.tinglabs.silent.party.model.Track;

/**
 * Created by Talal on 1/14/2017.
 */

public interface ViewController {
    void startLoader();

    void startLoader(String loadingText);

    void stopLoader();

    void showToast(String text);

    void showLoadingToast(String text);

    void dismissLoadingToast();

    void showError(String text);

    void navigateTo(int id);

    void setPartyTitle(String text);

    void setHeaderTitle(String text);

    void setHeaderStatus(String text);

    void setHeaderImage(String path);

    void showPlayer();

    void hidePlayer();

    void playTrack(Track track);

    void playTrack(Track track, int atTime);

    void pauseTrack();

    void setPlaylist(Playlist playlist);
}
