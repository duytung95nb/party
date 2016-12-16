package com.uitstu.party.models;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.supports.ChatItem;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by duy tung dao on 12/16/2016.
 */

public class Conversation {
    private String conversationID;
    private ArrayList<String> displayNames;
    private String lastMessage;
    private String lastUpdatedTime;
    private String partyName;
    private ArrayList<String> people;           // id người dùng

    private DatabaseReference conversation;
    private final Semaphore semaphore = new Semaphore(0);
    public Conversation(String conversationID) {
        this.conversationID = conversationID;
        conversation = FirebaseDatabase.getInstance().getReference()
                .child("conversations")         // lấy danh sách conversations từ node conversation tổng
                .child(this.conversationID);    // lấy ra conversation cụ thể
        this.partyName = conversation.child("partyName").getKey().toString();
        this.lastMessage = conversation.child("lastMessage").getKey().toString();
        this.lastUpdatedTime = conversation.child("lastUpdatedTime").getKey().toString();
        conversation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Conversation.this.setPartyName(dataSnapshot.child("partyName").toString());
                Conversation.this.setLastMessage(dataSnapshot.child("lastMessage").toString());
                Conversation.this.setLastUpdatedTime(dataSnapshot.child("lastUpdatedTime").toString());
                semaphore.release();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        try{
            semaphore.acquire();
        }catch(InterruptedException e){
            Log.d("interupt exception: ",e.toString());
        }

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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
}
