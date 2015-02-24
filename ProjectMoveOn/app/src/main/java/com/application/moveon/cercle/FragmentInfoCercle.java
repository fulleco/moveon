package com.application.moveon.cercle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.rest.modele.CerclePojo;

import org.w3c.dom.Text;

/**
 * Created by suparjam on 19/02/2015.
 */
public class FragmentInfoCercle extends Fragment {

    private static View view;
    private TextView textView;
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_infos_cercle, container, false);
        textView = (TextView) view.findViewById(R.id.text_view_titre);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // this is really important in order to save the state across screen
        // configuration changes for example
        setRetainInstance(true);

        mInflater = LayoutInflater.from(getActivity());

    }


    public void updateContent() {
        CerclePojo currentCercle =((HomeActivity)getActivity()).getCurrentCercle();
        if(currentCercle==null)
            return;

        textView.setText(currentCercle.getTitre());

    }
}
