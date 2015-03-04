package com.application.moveon.cercle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.cercle.FragmentInfoCercle;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.GetParticipants_Callback;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class InfoCercleAdapter extends BaseAdapter {

    private UserPojo[] list ;
    private Context context;
    private FragmentInfoCercle fragmentInfoCercle;
    private SessionManager session;


    public InfoCercleAdapter(UserPojo[] list, Context context, FragmentInfoCercle fragmentInfoCercle) {
        this.list = list;
        this.context = context;
        this.fragmentInfoCercle = fragmentInfoCercle;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int pos) {
        return list[pos];
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
            view = inflater.inflate(R.layout.layout_cercleinfo, null);
        }

        final UserPojo userPojo = list[position];
        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.label);
        listItemText.setText(userPojo.getFirstname() + " " + userPojo.getLastname());

        ImageView imgv = (ImageView)view.findViewById(R.id.icon);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .oval(false)
                .build();

        Picasso.with(context).load("http://martinezhugo.com/pfe/images/"+ userPojo.getId_client()+"/profile.jpg").resize(100, 100).transform(transformation).into(imgv);


        return view;
    }

    public UserPojo[] getList() {
        return list;
    }

    public void setList(UserPojo[] list) {
        this.list = list;
    }
}
