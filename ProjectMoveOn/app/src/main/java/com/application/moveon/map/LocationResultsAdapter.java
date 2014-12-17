package com.application.moveon.map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.application.moveon.R;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by damota on 17/12/2014.
 */
public class LocationResultsAdapter extends BaseAdapter {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    private int layoutId;

    private ArrayList<MarkerOptions> mData = new ArrayList<MarkerOptions>();

    private LayoutInflater mInflater;

    private static Activity activity;

    public LocationResultsAdapter(Activity activity, Context context, int layoutId, ArrayList<MarkerOptions> data) {

        this.layoutId = layoutId;
        this.mData = new ArrayList<MarkerOptions>(data);
        this.activity = activity;

        mInflater = LayoutInflater.from( context );

        notifyDataSetChanged();
    }

    public void addItem(final MarkerOptions item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MarkerOptions getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        final MarkerOptions data = mData.get(position);
        String address = data.getTitle();

        if ((view == null)||view.getTag()==null) {
            holder = new ViewHolder();
                    view = mInflater.inflate(R.layout.layout_item_location, null);
                    holder.textView = (TextView) view.findViewById(R.id.address);

				/*if((data.user_id.equals(user_id))&&(data.status.equals("accepted"))){
					holder.addButton.setBackground(view.getContext().getApplicationContext().getResources().getDrawable(R.drawable.check));
				}else{*/

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(address);

        return view;
    }

    public static class ViewHolder {
        public TextView textView;
    }

}
