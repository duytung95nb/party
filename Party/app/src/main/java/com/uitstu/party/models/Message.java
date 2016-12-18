package com.uitstu.party.models;

/**
 * Created by duy tung dao on 12/16/2016.
 */

public class Message {
    private String content;
    private long createdTime;
    private String user_id;
    public Message(){

    }
    public Message(String content, long createdTime, String user_id) {
        this.content = content;
        this.createdTime = createdTime;
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
