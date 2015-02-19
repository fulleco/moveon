package com.application.moveon.provider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i("ANTHO", "boot");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
		//int minutes = 0;
		//if(prefs.getBoolean("prefNotification", false)){
		//	minutes=Integer.parseInt(prefs.getString("prefNotificationFrequency", ""));
		//}
        int seconds = 5;
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
		Intent i = new Intent(context, ProviderService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0); 
		am.cancel(pi);
		if (seconds > 0) {
			//am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pi);
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + seconds*1000, seconds*1000, pi);
        }
	}

}
