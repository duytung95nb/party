package com.uitstu.party.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.supports.ChatItem;

import java.util.ArrayList;

/**
 * Created by duy tung dao on 12/16/2016.
 */

public class Conversation {
    private String conversationID;
    private String lastMessage;
    private String lastUpdatedTime;
    private String partyName;

    private DatabaseReference conversation;
    // gắn vào 1 chat item
    private ChatItem chatItem;
    public Conversation(String conversationID,ChatItem chat) {
        this.conversationID = conversationID;
        this.chatItem = chat;
        conversation = FirebaseDatabase.getInstance().getReference()
                .child("conversations")         // lấy danh sách conversations từ node conversation tổng
                .child(this.conversationID);    // lấy ra conversation cụ thể
        conversation.keepSynced(true);
        this.partyName = conversation.child("partyName").getKey().toString();
        this.lastMessage = conversation.child("lastMessage").getKey().toString();
        this.lastUpdatedTime = conversation.child("lastUpdatedTime").getKey().toString();
        conversation.addValueEventListener(new ValueEventListener() {
            // update this conversation+ update chat item
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String partyname = dataSnapshot.child("partyName").getValue().toString();
                String lastmessage = dataSnapshot.child("lastMessage").getValue().toString();
                String lastupdatedtime = dataSnapshot.child("lastUpdatedTime").getValue().toString();
                Conversation.this.setPartyName(partyname);
                Conversation.this.setLastMessage(lastmessage);
                Conversation.this.setLastUpdatedTime(lastupdatedtime);
                chatItem.RefreshContent(partyname,lastmessage,lastupdatedtime);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
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
