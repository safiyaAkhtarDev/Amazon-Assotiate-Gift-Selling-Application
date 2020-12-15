package com.safiyaakhtar.gifty.connection;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.safiyaakhtar.gifty.Activity.Home_Activity;
import com.safiyaakhtar.gifty.Activity.ItemsActivity;
import com.safiyaakhtar.gifty.POJO.NotyPOJO;
import com.safiyaakhtar.gifty.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    NotyPOJO notyPOJO=new NotyPOJO();
    ConnectionClass connectionClass=new ConnectionClass();

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }


    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle("Scheduled Notification")
//                .setAutoCancel(true)
//                .setContentTitle("Empty Pocket")
//                .setColor(getResources().getColor(R.color.colorAccent))
//                .setContentText("This notification has been triggered by Notification Service")
//                .setSmallIcon(R.drawable.blog_icon)
//                .setLargeIcon(R.drawable.load)
//                    .setStyle(new Notification.BigPictureStyle().bigPicture(result));
//

//
//
//
//                    .setLargeIcon(result)
//                    .setStyle(new Notification.BigPictureStyle().bigPicture(result))
//                    .build();
        ItemData itemData=new ItemData();
        itemData.execute();

//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                NOTIFICATION_ID,
//                new Intent(this, Home_Activity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));
//
//        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(NOTIFICATION_ID, builder.build());
    }
    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl,sendto,searchtext;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl, String SendTo, String SearchText) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
            this.sendto=SendTo;
            this.searchtext=SearchText;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            Intent intent = new Intent(mContext, ItemsActivity.class);
            intent.putExtra("id",Integer.parseInt(sendto));
            intent.putExtra("message",searchtext);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = new Notification.Builder(mContext)
                    .setSmallIcon(R.mipmap.logo)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(result)
                    .setSubText(message)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                    .build();
//              notif.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(getApplicationContext()))
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.defaults |= Notification.DEFAULT_SOUND;
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(1, notif);
        }
    }
    public class ItemData extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Check Your Internet Connection";
                } else {
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());
                    Log.d("Safiya",dates);
                    String querymen ="select Top 1 * from NotificationManager where date='"+dates+"' order by date DESC";

                    Statement stmt = con.createStatement();
                    ResultSet rs = null;
                    rs = stmt.executeQuery(querymen);
                    if (rs.next()) {
                        isSuccess = true;
                        notyPOJO.setTitle(rs.getString("Title"));
                        notyPOJO.setBigIcon(rs.getString("BigIcon"));
                        notyPOJO.setMessage(rs.getString("Message"));
                        notyPOJO.setSento(rs.getString("SendTo"));
                        notyPOJO.setSearchText(rs.getString("SearchText"));
                    }else{
                        isSuccess=false;
                    }
                }
            } catch (SQLException ex) {
                isSuccess = false;
                z = "Check Your Internet Connection";
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isSuccess){
                new generatePictureStyleNotification(getApplicationContext(),notyPOJO.getTitle(), notyPOJO.getMessage(),
                        notyPOJO.getBigIcon(),notyPOJO.getSento(),notyPOJO.getSearchText()).execute();
            }else{

            }
        }
    }
}