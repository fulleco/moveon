package com.application.moveon.map;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.moveon.R;

/**
 * Created by Hugo on 10/01/2015.
 */
public class FragmentContactList extends Fragment {

    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location, container, false);

        activity = (FragmentActivity)getActivity();

        return view;

    }
}
