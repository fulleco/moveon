package com.application.moveon.cercle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.application.moveon.R;
import com.application.moveon.model.MessagePojo;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschene on 19/02/2015.
 */
public class FragmentListMessage extends Fragment {

    private static View view;
    private LayoutInflater mInflater;
    private ListView list_messages;
    MoveOnDB moveOnDB;

    SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_messages, container, false);
        list_messages = (ListView)view.findViewById(R.id.list_view);

        session = new SessionManager(getActivity());

        updateView();

        return view;
    }

    public void updateView() {

        if(list_messages==null)
            return;

        moveOnDB = new MoveOnDB(this.getActivity().getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        moveOnDB.open();
        ArrayList<MessagePojo> messagePojos = moveOnDB.getMessages(session.getUserDetails().get(SessionManager.KEY_ID));
        moveOnDB.close();

        if(messagePojos==null) {
            list_messages.setAdapter(null);
            return;
        }

        final ListMessagesAdapter a = new ListMessagesAdapter(messagePojos, getActivity());

        list_messages.setAdapter(a);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // this is really important in order to save the state across screen
        // configuration changes for example
        setRetainInstance(true);

        mInflater = LayoutInflater.from(getActivity());
    }
}
