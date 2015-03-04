package com.application.moveon.provider;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.AddMessage_Callback;
import com.application.moveon.rest.callback.GetMessage_Callback;
import com.application.moveon.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProviderReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {

        NotificationManager notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        SessionManager session = new SessionManager(context);
        MoveOnService mainmos = (new RestClient(true)).getApiService();
        String action = intent.getAction();

        if(GetMessage_Callback.OK_ACTION.equals(action)) {
            String sender = intent.getStringExtra("SENDER");
            String receiver = intent.getStringExtra("RECEIVER");
            String circle = intent.getStringExtra("CIRCLE");
            mainmos.addMessage(circle, receiver, sender, "Ok", new SimpleDateFormat("dd-MM-yyyy'T'HH:mm").format(new Date()),
                    0,"holo_check", new AddMessage_Callback(null, null));
            notifManager.cancel(Integer.valueOf(sender));

        }
	}

}
