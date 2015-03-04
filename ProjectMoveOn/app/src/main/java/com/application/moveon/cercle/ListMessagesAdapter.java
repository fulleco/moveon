package com.application.moveon.cercle;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.model.MessagePojo;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class ListMessagesAdapter extends BaseAdapter {

    private ArrayList<MessagePojo> list = new ArrayList<MessagePojo>();
    private Context context;



    public ListMessagesAdapter(ArrayList<MessagePojo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_messagelist, null);
        }


        final MessagePojo messagePojo = list.get(position);
        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.label);
        listItemText.setText(messagePojo.getFirstname_sender() + ": " + messagePojo.getContent());

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        int id_image = messagePojo.getId_image();
        if(id_image!=-1) {
            Drawable d = context.getResources().getDrawable(id_image);
            icon.setImageDrawable(d);
        }

        LinearLayout linear_cercle_list = (LinearLayout) view.findViewById(R.id.linear_cercle_list);
        linear_cercle_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        return view;
    }

    public ArrayList<MessagePojo> getList() {
        return list;
    }

    public void setList(ArrayList<MessagePojo> list) {
        this.list = list;
    }
}
