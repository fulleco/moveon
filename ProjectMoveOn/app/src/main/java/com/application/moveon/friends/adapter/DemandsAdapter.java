package com.application.moveon.friends.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.AnswerDemand_Callback;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by Hugo on 26/01/2015.
 */
public class DemandsAdapter extends BaseAdapter {
    private Activity activity;
    private List<DemandsPojo> d;
    private String mail;

    public DemandsAdapter(Activity a, List<DemandsPojo> d, String mail){
        this.activity = a;
        this.d = d;
        this.mail = mail;
    }
    @Override
    public int getCount() {
        return d.size();
    }

    @Override
    public Object getItem(int i) {
        return d.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup viewGroup) {

        RestClient r = new RestClient(false);
        final SessionManager session = new SessionManager(activity);
        final MoveOnService mos = r.getApiService();
        final ProgressDialog p = new ProgressDialog(activity);
        final Context context = this.activity.getBaseContext();
        final MoveOnDB db = new MoveOnDB(context, session.getUserDetails().get(SessionManager.KEY_EMAIL));

        View view = convertview;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_demandslist, null);
        }
        final DemandsPojo dp = d.get(position);

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.name_profile);
        listItemText.setText(dp.getSender());
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCancelable(true);
        p.setMessage("Prise en compte de la réponse...");


        //Handle buttons and add onClickListeners
        ImageView imgv = (ImageView) view.findViewById(R.id.image_profile);

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .oval(false)
                .build();

        Picasso.with(context).load("http://martinezhugo.com/pfe/images/"+ dp.getId()+"/profile.jpg").transform(transformation).into(imgv);

        ImageButton imgb1 = (ImageButton) view.findViewById(R.id.cancel);
        imgb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p.show();
                mos.answerdemand(dp.getMail(), mail, false, new AnswerDemand_Callback(activity, p, dp.getMail(), false, db));
                d.remove(position);
                db.open();
                db.deleteDemand(dp.getId());
                db.close();
                notifyDataSetChanged();
            }
        });
        ImageButton imgb2 = (ImageButton) view.findViewById(R.id.accept);

        imgb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p.show();
                mos.answerdemand(dp.getMail(), mail, true, new AnswerDemand_Callback(activity, p, dp.getMail(), true, db));
                d.remove(position);
                db.open();
                db.deleteDemand(dp.getId());
                db.close();
                notifyDataSetChanged();
            }
        });


        return view;

    }
}
