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

public class ProviderReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {

        NotificationManager notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        SessionManager session = new SessionManager(context);
        MoveOnService mainmos = (new RestClient(true)).getApiService();
        String action = intent.getAction();

        if(GetMessage_Callback.OK_ACTION.equals(action)) {
            mainmos.addMessage("0", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Ok", "DATE",
                    0, new AddMessage_Callback(null, null));
        }
        notifManager.cancel(0);
	}

}
