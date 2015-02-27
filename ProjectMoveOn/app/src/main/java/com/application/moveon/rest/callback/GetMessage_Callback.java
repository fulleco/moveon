package com.application.moveon.rest.callback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.model.MessagePojo;
import com.application.moveon.provider.ProviderReceiver;
import com.application.moveon.tools.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by user on 18/02/2015.
 */
public class GetMessage_Callback implements Callback<MessagePojo[]> {

    private NotificationManager notifManager;
    private Context context;

    public static String OK_ACTION = "OK";

    public GetMessage_Callback(Context context){
        this.context = context;
    }

    @Override
    public void success(MessagePojo[] messagePojos, Response response) {

        if(messagePojos == null) return;
        ArrayList<MessagePojo> messages = new ArrayList<MessagePojo>(Arrays.asList(messagePojos));
        Collections.sort(messages);
        for(MessagePojo m : messages){
            createNotification(m);
        }
    }

    public void createNotification(MessagePojo m) {

        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("SENDER", m.getId_sender());
        intent.putExtra("RECEIVER", m.getId_receiver());
        intent.putExtra("CIRCLE", m.getId_circle());

        Intent intentOk = new Intent(context, ProviderReceiver.class);
        intentOk.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentOk.putExtra("SENDER", m.getId_sender());
        intentOk.putExtra("RECEIVER", m.getId_receiver());
        intentOk.putExtra("CIRCLE", m.getId_circle());
        intentOk.setAction(OK_ACTION);

        PendingIntent pIntentOk = PendingIntent.getBroadcast(context, 1,
                intentOk, 0);

        // intent.putExtra("mail", email);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        StringBuilder text = new StringBuilder();
        StringBuilder longText = new StringBuilder();
        String title = "Nouveau message de " + m.getFirstname_sender();

        text.append("Découvrez-le vite !");

        longText.append(m.getContent());

        Notification.Builder noti = null;
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification;
        noti = new Notification.Builder(context).setContentTitle(title)
                .setContentText(text.toString())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("MoveOn")
                .setWhen((new Date()).getTime())
                .setContentIntent(pIntent)
                .addAction(R.drawable.holo_map, "Voir la carte", pIntent)
                .addAction(R.drawable.holo_check, "Ok", pIntentOk);

        if (!text.toString().equals(longText.toString())) {
            noti.setStyle(
                    new Notification.BigTextStyle().bigText(longText
                            .toString())).setContentIntent(pIntent);
        }
        notification = noti.build();

        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;

        if (!text.toString().equals("") && !text.toString().equals(null)) {
            notifManager.notify(m.getId_sender().hashCode(), notification);
        }
    }

    @Override
    public void failure(RetrofitError error) {
    }
}
