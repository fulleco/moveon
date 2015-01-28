package com.application.moveon.Friends;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.application.moveon.R;
import com.application.moveon.rest.modele.UserPojo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hugo on 13/01/2015.
 */
public class UserAdapter extends BaseAdapter {

    private ArrayList<UserPojo> list = new ArrayList<UserPojo>();
    private Context context;


    public UserAdapter(ArrayList<UserPojo> list, Context context) {
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
            view = inflater.inflate(R.layout.layout_userlist, null);
        }
        UserPojo us = list.get(position);
        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.label);
        listItemText.setText(us.getFirstname());



        //Handle buttons and add onClickListeners
        ImageView imgv = (ImageView) view.findViewById(R.id.icon);
        Picasso.with(context).load("http://martinezhugo.com/pfe/images/"+ us.getId_client()+"/profile.jpg").into(imgv);



        return view;
    }
}
