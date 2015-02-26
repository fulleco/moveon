package com.application.moveon.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.application.moveon.R;
import com.application.moveon.cercle.FragmentInfoCercle;
import com.application.moveon.cercle.FragmentListCercle;

public class FragmentSlidingMenu extends Fragment implements OnTabChangeListener {

    private static View view;
    private TabHost mTabHost;
    private int mCurrentTab;
    private FragmentInfoCercle fragmentInfoCercle;
    private FragmentListCercle fragmentListCercle;

    public static final String TAB_INFO_CERCLE = "Infos cercle";
    public static final String TAB_LIST_CERCLE = "Liste cercle";

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sliding_menu, container, false);

        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);

        fragmentInfoCercle = new FragmentInfoCercle();
        fragmentListCercle = new FragmentListCercle();



        fragmentListCercle.setTargetFragment(fragmentInfoCercle,0);

        setupTabs();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(mCurrentTab);
        // manually start loading stuff in the first tab
        updateTab(TAB_INFO_CERCLE, R.id.tab_1);
    }

    private void updateTab(String tabId, int placeholder) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(tabId) == null) {
            if(tabId==TAB_INFO_CERCLE)
                fm.beginTransaction()
                        .replace(placeholder, fragmentInfoCercle, tabId)
                        .commit();
            if(tabId==TAB_LIST_CERCLE)
                fm.beginTransaction()
                        .replace(placeholder, fragmentListCercle, tabId)
                        .commit();
        }
    }

    private void setupTabs() {
        mTabHost.setup(); // you must call this before adding your tabs!
        mTabHost.addTab(newTab(TAB_INFO_CERCLE, R.string.tab_info_cercle, R.id.tab_1));
        mTabHost.addTab(newTab(TAB_LIST_CERCLE, R.string.tab_list_cercle, R.id.tab_2));

    }

    private TabHost.TabSpec newTab(String tag, int labelId, int tabContentId) {


        View indicator = LayoutInflater.from(getActivity()).inflate(
                R.layout.tab,
                (ViewGroup) view.findViewById(android.R.id.tabs), false);
        ((TextView) indicator.findViewById(R.id.text)).setText(labelId);

        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(tabContentId);
        return tabSpec;
    }


    @Override
    public void onTabChanged(String tabId) {

        if (TAB_INFO_CERCLE.equals(tabId)) {
            updateTab(tabId, R.id.tab_1);
            mCurrentTab = 0;
            return;
        }
        if (TAB_LIST_CERCLE.equals(tabId)) {
            updateTab(tabId, R.id.tab_2);
            mCurrentTab = 1;
            return;
        }
    }
}