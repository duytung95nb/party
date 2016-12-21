package com.uitstu.party.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
}