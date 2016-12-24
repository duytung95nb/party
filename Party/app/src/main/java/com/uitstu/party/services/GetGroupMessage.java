package com.uitstu.party.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uitstu.party.MainActivity;
import com.uitstu.party.models.User;
import com.uitstu.party.presenter.PartyFirebase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by duy tung dao on 12/24/2016.
 */

public class GetGroupMessage extends Service {
    private static Context mContext;
    private DatabaseReference currentGroupChat;
    private ChildEventListener currentGroupChatListener;
    public static void setContext(Context context){
        mContext = context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        currentGroupChat = FirebaseDatabase.getInstance().getReference()
                .child(PartyFirebase.user.curPartyID)
                .child("messages");
        currentGroupChatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String mContent = dataSnapshot.child("content").getValue().toString();

                long mCreatedTime = Long.parseLong(dataSnapshot.child("createdTime").getValue().toString());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM hh:mm:ss");
                String dateString = formatter.format(new Date(mCreatedTime));

                String mUserID = dataSnapshot.child("user_id").getValue().toString();
                String userName = "";
                // lấy user name từ user id
                for (User u:PartyFirebase.users) {
                    if(u.UID.equals("mUserID"))
                        userName=u.name;
                }

                createNotification(userName,mContent,dateString);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
    // bắt đầu lệnh
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(currentGroupChatListener!=null&& currentGroupChat!=null){
            currentGroupChat.addChildEventListener(currentGroupChatListener);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(currentGroupChatListener!=null&& currentGroupChat!=null){
            currentGroupChat.removeEventListener(currentGroupChatListener);
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        createNotification("Low memory", "Your phone's memory is low", "Check phone's memory");
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotification(String title, String text, String info){
        // now is notification
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(mContext);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setTicker("Hearty365")
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(contentIntent)
                .setContentInfo(info)
                .setSound(alarmSound);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

        // if the screeen is off, light it up
        PowerManager pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        if(pm.isScreenOn()==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }
}
