package com.uitstu.party.models;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.MainActivity;
import com.uitstu.party.supports.ChatItem;

/**
 * Created by duy tung dao on 12/20/2016.
 */

public class RecentGroupConversation {
    private String groupID;
    private String lastMessage;
    private String lastUpdatedTime;
    private String partyName;

    private DatabaseReference groupConversation;
    // gắn vào 1 chat item
    private ChatItem chatItem;

    public RecentGroupConversation(String groupID, ChatItem chat) {
        this.groupID = groupID;
        this.chatItem = chat;
        groupConversation = FirebaseDatabase.getInstance().getReference()
                .child("parties")
                .child(this.groupID);        // xuống tới thẻ group id, đại diện cho 1 group
        groupConversation.keepSynced(true);
        this.partyName = groupConversation.child("name").getKey().toString();
        this.lastMessage = groupConversation.child("conversation/lastMessage").getKey().toString();
        this.lastUpdatedTime = groupConversation.child("conversation/lastUpdatedTime").getKey().toString();
        groupConversation.addValueEventListener(new ValueEventListener() {
            // update this conversation+ update chat item
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String partyname = dataSnapshot.child("name").getValue().toString();
                String lastmessage = dataSnapshot.child("conversation/lastMessage").getValue().toString();
                String lastupdatedtime = dataSnapshot.child("conversation/lastUpdatedTime").getValue().toString();
                RecentGroupConversation.this.setPartyName(partyname);
                RecentGroupConversation.this.setLastMessage(lastmessage);
                RecentGroupConversation.this.setLastUpdatedTime(lastupdatedtime);
                chatItem.RefreshContent(partyname, lastmessage, lastupdatedtime);
                // nếu main activity đang không chạy thì hiện notification
                if (MainActivity.isRunning==false){
                    createNotification(chatItem.getContext(),partyname,lastmessage,lastupdatedtime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
    private void createNotification(Context context, String title, String text, String info){
        // now is notification
        Intent intent = new Intent(context,
                MainActivity.class);
        intent.putExtra("NavigateMessage","NavigateToChatDetail");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
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

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

        // if the screeen is off, light it up
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if(pm.isScreenOn()==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }
}