package com.application.moveon.provider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.GetMessage_Callback;
import com.application.moveon.session.SessionManager;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by user on 18/02/2015.
 */
public class ProviderService extends Service {
    private PowerManager.WakeLock mWakeLock;
    private Context context = this;
    private NotificationManager notifManager;
    private SessionManager session;
    private String idUser;
    private MoveOnService mainmos;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ANTHO", "bind");
        return null;
    }

    private void handleIntent(Intent intent) {

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Notification Service");
        mWakeLock.acquire();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            stopSelf();
            return;
        }

        session = new SessionManager(ProviderService.this);
        session.checkLogin(false);
        idUser = session.getUserDetails().get(SessionManager.KEY_ID);
        //new NotifTask().execute();

        mainmos.getmessages(idUser, new GetMessage_Callback(this));
    }
/*
    private class NotifTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.i("ANTHO", "background");

            try {
                getNotif();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("ANTHO", "post");
            stopSelf();
        }

        public void createNotification(String type, String id_user,
                                       String login_sender) {

            Intent intent = new Intent(context, PhotoActivity.class);
            // intent.putExtra("mail", email);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            notifManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
            StringBuilder text = new StringBuilder();
            StringBuilder longText = new StringBuilder();
            String title = "FlipIt";

            if(type.equals("demande")){
                text.append(login_sender + " veut vous ajouter en tant qu'ami sur FlipIt !");
            }else{
                text.append("Nouveau message de " + login_sender);
            }

            longText.append("Venez vite le découvrir!");

            Notification.Builder noti = null;
            Notification notification;
            noti = new Notification.Builder(context).setContentTitle(title)
                    .setContentText(text.toString())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("Flip it !")
                    .setContentIntent(pIntent);

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

        public boolean getNotif() throws InterruptedException,
                ExecutionException, JSONException {

            InputStream is = null;
            ArrayList<NotificationsData> result = new ArrayList<NotificationsData>();

            // http post
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httppost = new HttpGet(
                        "http://mobile.linkibe.fr/appli_hiddenphoto/get_notification.php?idUser="
                                + idUser);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String resultString = "";
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                resultString = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            JSONArray jArray = null;
            jArray = new JSONArray(resultString);

            if (jArray == null)
                return false;

            final int length = jArray.length();

            if (length == 0)
                return false;

            for (int i = 0; i < length; i++) {

                Log.i("ANTHO", "notif trouvee");

                JSONObject row_item = null;
                try {
                    row_item = jArray.getJSONObject(i);
                    String id = row_item.getString("id");
                    String type = row_item.getString("type");
                    String id_user = row_item.getString("id_user");
                    String id_sender = row_item.getString("id_sender");
                    String login_sender = row_item.getString("login");

                    NotificationsData item = new NotificationsData(id, type,
                            id_user, login_sender);
                    result.add(item);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (NotificationsData n : result) {
                    createNotification(n.type, n.id_user, n.id_sender);
                }
            }
            return true;
        }

    }
    */

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("ANTHO", "start");
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ANTHO", "startcommand");
        RestClient r = new RestClient(false);
        mainmos = (new RestClient(true)).getApiService();
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("ANTHO", "destroy");
        super.onDestroy();
        mWakeLock.release();
    }
}