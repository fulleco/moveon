package com.application.moveon.cercle;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class ListCercleAdapter extends BaseAdapter {

    private ArrayList<CerclePojo> list = new ArrayList<CerclePojo>();
    private Context context;
    private FragmentInfoCercle fragmentInfoCercle;


    public ListCercleAdapter(ArrayList<CerclePojo> list, Context context, FragmentInfoCercle fragmentInfoCercle) {
        this.list = list;
        this.context = context;
        this.fragmentInfoCercle = fragmentInfoCercle;
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
            view = inflater.inflate(R.layout.layout_cerclelist, null);
        }

        final CerclePojo cerclePojo = list.get(position);
        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.label);
        listItemText.setText(cerclePojo.getTitre());


        LinearLayout linear_cercle_list = (LinearLayout) view.findViewById(R.id.linear_cercle_list);
        linear_cercle_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((HomeActivity) fragmentInfoCercle.getActivity()).setCurrentCercle(cerclePojo);
                fragmentInfoCercle.updateContent();
            }
        });

        return view;
    }
}
