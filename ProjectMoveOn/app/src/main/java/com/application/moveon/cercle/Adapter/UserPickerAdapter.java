package com.application.moveon.cercle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.moveon.R;
import com.application.moveon.rest.modele.UserPojo;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Hugo on 02/03/2015.
 */
public class UserPickerAdapter extends BaseAdapter{

    private ArrayList<UserPojo> list;
    private ArrayList<UserPojo> selected;
    private Context context;


    public ArrayList<UserPojo> getselected(){
        return selected;
    }

    public UserPickerAdapter(ArrayList<UserPojo> list,ArrayList<UserPojo> selected, Context context) {
        this.list = list;
        this.context = context;
        this.selected = selected;
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
            view = inflater.inflate(R.layout.layout_userpicker, null);
        }
        final UserPojo us = list.get(position);
        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.label);
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_friend);

        if(selected.contains(us)){
            checkBox.setChecked(true);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                  selected.add(us);
                }else{
                   selected.remove(us);
                }
            }
        });

        listItemText.setText(us.getFirstname() +" "+ us.getLastname());

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .oval(false)
                .build();

        //Handle buttons and add onClickListeners
        ImageView imgv = (ImageView) view.findViewById(R.id.icon);
        Picasso.with(context).load("http://martinezhugo.com/pfe/images/"+ us.getId_client()+"/profile.jpg").transform(transformation).resize(100, 100).into(imgv);



        return view;
    }
}
