package com.rackluxury.ferrari.reddit.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import com.rackluxury.ferrari.R;
import com.rackluxury.ferrari.reddit.events.ChangeShowAvatarOnTheRightInTheNavigationDrawerEvent;
import com.rackluxury.ferrari.reddit.utils.SharedPreferencesUtils;

public class NavigationDrawerPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(SharedPreferencesUtils.NAVIGATION_DRAWER_SHARED_PREFERENCES_FILE);
        setPreferencesFromResource(R.xml.navigation_drawer_preferences, rootKey);

        SwitchPreference showAvatarOnTheRightSwitch = findPreference(SharedPreferencesUtils.SHOW_AVATAR_ON_THE_RIGHT);

        if (showAvatarOnTheRightSwitch != null) {
            showAvatarOnTheRightSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeShowAvatarOnTheRightInTheNavigationDrawerEvent((Boolean) newValue));
                return true;
            });
        }
    }
}