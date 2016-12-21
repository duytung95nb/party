package com.uitstu.party.models;

import java.util.List;

/**
 * Created by duy tung dao on 12/20/2016.
 */

public class GroupConversation {
    public String lastMessage;
    public String lastUpdatedTime;
    public GroupConversation(){
        lastMessage= "0";
        lastUpdatedTime = "0";
    }

    public GroupConversation(String lastMessage, String lastUpdatedTime) {
        this.lastMessage = lastMessage;
        this.lastUpdatedTime = lastUpdatedTime;
    }
}
