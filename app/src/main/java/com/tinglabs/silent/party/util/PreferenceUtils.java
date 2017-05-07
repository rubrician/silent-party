package com.tinglabs.silent.party.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 1/17/2017.
 */

public class PreferenceUtils {
    private static final String PREFERENCE = "com.tinglabs.silent.party";

    private static final String PREF_REGISTRATION = "pref_registered";
    private static final String PREF_LAST_ACTIVE_FRAGMENT = "pref_last_active_fragment";
    private static final String PREF_REMEMBER_ME = "pref_remember";
    private static final String PREF_MOCK_MODE = "pref_mock_mode";

    public static void setRegistration(Context context, User user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_REGISTRATION, user.getUserName());
        editor.apply();
    }

    public static String getRegistration(Context context, String userName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_REGISTRATION, "");
    }

    public static void setLastActiveFragment(Context context, int id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_LAST_ACTIVE_FRAGMENT, id);
        editor.apply();
    }

    public static String getLastActiveFragment(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_LAST_ACTIVE_FRAGMENT, "");
    }

    public static void setRemembered(Context context, boolean remember) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_REMEMBER_ME, remember);
        editor.apply();
    }

    public static boolean isRemembered(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_REMEMBER_ME, false);
    }
}
