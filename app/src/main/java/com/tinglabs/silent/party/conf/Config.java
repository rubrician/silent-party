package com.tinglabs.silent.party.conf;

import java.util.ArrayList;
import java.util.List;

import com.tinglabs.silent.party.model.Track;

/**
 * Created by Talal on 12/24/2016.
 */

public class Config {

    // @formatter:off
    public static int DEFAULT_SERVER_PORT               = 8080;
    public static int DEFAULT_CLIENT_PORT               = 8990;
    public static int DEFAULT_SOCKET_TIMEOUT            = 5000;

    public static int DEFAULT_DISCOVERY_TIMEOUT         = 30; // in seconds

    public static int DEFAULT_SYNC_TIME                 = 5; // in seconds

    public static final String SOUND_CLOUD_ID           = ""; // Add your sound cloud ID here

    public static final String DEFAULT_MY_PLAYLIST      = "MyPlaylist";
    public static final String DEFAULT_PARTY_PLAYLIST   = "PartyPlaylist";

    public static Boolean MOCK_MODE                     = false;

    public static final String APP_DIR_NAME             = "SilentParty";

    // These are the preloaded tracks when the app first installs.
    public static final List<Track> DEFAULT_TRACKS      = new ArrayList<Track>() {{
        add(new Track("28076261", "DJ Krush - Beyond Raging Waves", "Jaku", "https://api.soundcloud.com/tracks/28076261/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", null, DEFAULT_MY_PLAYLIST));
        add(new Track("42328219", "The xx - Intro", "Dubstep Remix (Go Jane Go)", "https://api.soundcloud.com/tracks/42328219/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000021235374-lb83wa-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("170009274", "Indila - Dernière Danse (IV)", "Indila - Dernière Danse (IV)", "https://api.soundcloud.com/tracks/170009274/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000092544901-uy495r-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("244184510", "Faded - Alan Walker", "Extended Edition", "https://api.soundcloud.com/tracks/244184510/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000144901583-eo0kye-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("232487805", "Way Down We Go", "Kaleo", "https://api.soundcloud.com/tracks/232487805/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-JFhhewF0diTq-0-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("156066566", "Indila - Ego & Oud", "Orient Cover (by Ersin Ersavas)", "https://api.soundcloud.com/tracks/156066566/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000083444679-l3ozar-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("44619584", "idenline - Together", "VIP", "https://api.soundcloud.com/tracks/44619584/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000022429094-fubsu9-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("295346947", "Natalie Taylor- In The Air Tonight", "lucifer fox", "https://api.soundcloud.com/tracks/295346947/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000195948825-mf64b4-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("217488904", "Elliphant - Best People In The World", "Ganco Weekend Bboy Edit", "https://api.soundcloud.com/tracks/217488904/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000125141518-my2o8y-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("79081413", "Ludovico Einaudi - Experience", "In a Time Lapse", "https://api.soundcloud.com/tracks/79081413/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000040626574-pkblc0-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("11154623", "Micheal jackson They don't really care about us", "Remix", "https://api.soundcloud.com/tracks/11154623/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000005145741-x8ri3z-large.jpg", DEFAULT_MY_PLAYLIST));
        add(new Track("30694177", "Edward Maya - Stereo Love", "Original Mix", "https://api.soundcloud.com/tracks/30694177/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000015398559-bej14o-large.jpg", DEFAULT_MY_PLAYLIST));
        //add(new Track("38654661", "Elvira T - Все решено", "Elvira T", "https://api.soundcloud.com/tracks/38654661/stream?client_id=aecd29173d71cbc97711fb5b37cb839c", "https://i1.sndcdn.com/artworks-000019384150-urx5p8-large.jpg", DEFAULT_MY_PLAYLIST));
    }};
    // @formatter:on
}
