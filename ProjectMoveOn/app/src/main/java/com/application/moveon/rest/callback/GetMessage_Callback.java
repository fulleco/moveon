package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.friends.UserAdapter;
import com.application.moveon.model.Message;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by user on 18/02/2015.
 */
public class GetMessage_Callback implements Callback<Message[]> {

    private NotificationManager notifManager;
    private Context context;

    public GetMessage_Callback(Context context){
        this.context = context;
    }

    @Override
    public void success(Message[] messagePojos, Response response) {

        if(messagePojos == null) return;
        ArrayList<Message> messages = new ArrayList<Message>(Arrays.asList(messagePojos));
        Collections.sort(messages);
        for(Message m : messages){
            createNotification(m.getContent(), m.getFirstname_sender() + " " + m.getLastname_sender());
        }
    }

    public void createNotification(String content, String sender) {

        Intent intent = new Intent(context, HomeActivity.class);
        // intent.putExtra("mail", email);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        StringBuilder text = new StringBuilder();
        StringBuilder longText = new StringBuilder();
        String title = "Nouveau message !";

        text.append("Nouveau message de : " + sender);

        longText.append("TEST" + content);

        Notification.Builder noti = null;
        Notification notification;
        noti = new Notification.Builder(context).setContentTitle(title)
                .setContentText(text.toString())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("MoveOn !")
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher, "En retard !", pIntent)
                .addAction(R.drawable.ic_launcher, "J'arrive !", pIntent);

        if (!text.toString().equals(longText.toString())) {
            noti.setStyle(
                    new Notification.BigTextStyle().bigText(longText
                            .toString())).setContentIntent(pIntent);
        }
        notification = noti.build();

        notification.flags = Notification.DEFAULT_LIGHTS
                | Notification.FLAG_AUTO_CANCEL;

        if (!text.toString().equals("") && !text.toString().equals(null)) {
            notifManager.notify(0, notification);
        }
    }

    @Override
    public void failure(RetrofitError error) {
    }
}
