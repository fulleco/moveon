package com.application.moveon.cercle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.moveon.R;
import com.application.moveon.model.MessagePojo;
import com.application.moveon.session.SessionManager;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class ListMessagesAdapter extends BaseAdapter {

    private ArrayList<MessagePojo> list = new ArrayList<MessagePojo>();
    private Context context;
    private SessionManager session;



    public ListMessagesAdapter(ArrayList<MessagePojo> list, Context context) {
        this.list = list;
        this.context = context;
        session = new SessionManager(context);
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
        final MessagePojo messagePojo = list.get(position);



        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(messagePojo.getId_sender().equals(session.getUserDetails().get(SessionManager.KEY_ID))){
            view = inflater.inflate(R.layout.layout_messagelist_sent, null);
        }else{
            view = inflater.inflate(R.layout.layout_messagelist, null);
        }






        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.label);
        listItemText.setText(messagePojo.getFirstname_sender());

        TextView contentText = (TextView) view.findViewById(R.id.content);
        contentText.setText(messagePojo.getContent());
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.linear_cercle_list);
        rl.setHorizontalGravity(Gravity.RIGHT);



        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        String id_image = messagePojo.getId_image();
        if(id_image!=null) {
            try {

                Resources resources = context.getResources();
                final int resourceId = resources.getIdentifier(id_image, "drawable",
                        context.getPackageName());
                Drawable d = context.getResources().getDrawable(resourceId);
                icon.setImageDrawable(d);
            }
            catch (Resources.NotFoundException e)
            {
                Log.d("MESSAGE : ",e.toString());
                Drawable d = context.getResources().getDrawable(R.drawable.ic_launcher);
                icon.setImageDrawable(d);
            }
        }


        rl.setOnClickListener(new View.OnClickListener() {
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
