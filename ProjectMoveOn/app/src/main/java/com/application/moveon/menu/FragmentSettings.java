package com.application.moveon.menu;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.application.moveon.R;

/**
 * Created by Quentin Bitschene on 20/02/2015.
 */
public class FragmentSettings extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


    }

}
