package com.application.moveon.menu;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;

/**
 * Created by Quentin Bitschene on 20/02/2015.
 */
public class FragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private View view;
    private SwitchPreference prefSync;
    private SwitchPreference prefLoc;
    private ListPreference prefFreq;
    private HomeActivity myActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        myActivity =  (HomeActivity) getActivity();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // just update all
        //pref_sync

       if(key.equals(getResources().getString(R.string.pref_sync_key)))
       {
           Log.d("QUENTIN : ", "LISTENING! - Pref changed for: " + key + " pref: " +
                   sharedPreferences.getBoolean(key,false));
           /*
           if(sharedPreferences.getBoolean(key,true))
                myActivity.startNotification();
           else
                myActivity.stopNotification();
                */

       }else if (key.equals(getResources().getString(R.string.pref_loc_key))) {
           Log.d("QUENTIN : ", "LISTENING! - Pref changed for: " + key + " pref: " +
                   sharedPreferences.getBoolean(key,false));

       }else if (key.equals(getResources().getString(R.string.pref_freq_key))) {
            myActivity.changeNotificationFrequency();
       }


    }

}
