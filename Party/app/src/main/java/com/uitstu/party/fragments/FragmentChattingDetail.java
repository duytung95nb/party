package com.uitstu.party.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.R;
import com.uitstu.party.models.Message;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.supports.ChatMessage;
import com.uitstu.party.supports.MemberAvatars;

import java.sql.Date;
import java.util.Map;

/**
 * Created by duy tung dao on 12/10/2016.
 */

public class FragmentChattingDetail extends Fragment {
    private ScrollView chatContentScrollView;
    private LinearLayout chatContentLayoutContainer;
    private LinearLayout chatToolsBar;
    private EditText edt_input_content;
    private Button btn_send;
    private FloatingActionButton btn_back;
    private FragmentChatting parentFragment;
    private RelativeLayout fragment_chatting_detail_layout;

    private DatabaseReference currentConversationContent;           // chứa nội dung các conversation
    private DatabaseReference currentMessages;           // chứa nội dung các message
    private ChildEventListener currentmessageChildEventListener;    // bộ lắng nghe sự kiện child
    private boolean isGroupChat;
    private final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_detail, container, false);
        // cố định cho cả normal chat và group chat
        fragment_chatting_detail_layout = (RelativeLayout) view.findViewById(R.id.fragment_chatting_detail_layout);
        chatContentScrollView = (ScrollView) view.findViewById(R.id.chatContentScrollView);
        chatContentLayoutContainer = (LinearLayout) view.findViewById(R.id.chatContentLayoutContainer);
        chatToolsBar = (LinearLayout)view.findViewById(R.id.chatToolsBar);
        edt_input_content = (EditText) view.findViewById(R.id.edt_input_content);
        edt_input_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            FragmentChattingDetail.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            }
        });
        btn_back = (FloatingActionButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FragmentChattingDetail.this.parentFragment!=null){
                    parentFragment.switchToNextFragment();
                    parentFragment.removeDetailFragment(FragmentChattingDetail.this);
                }
            }
        });
        btn_send = (Button) view.findViewById(R.id.btn_send);
        // lăn thanh cuộn xuống dưới cùng
        chatContentScrollView.post(new Runnable() {
            @Override
            public void run() {
                chatContentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        if(parentFragment!=null){
            // không cố định
            // nếu là group chat
            if(isGroupChat){
                // lấy mã user -> lấy mã party -> lấy messages trong conversation party

                currentConversationContent = FirebaseDatabase.getInstance().getReference()
                        .child("parties")
                        .child(PartyFirebase.user.curPartyID)
                        .child("conversation");
                currentConversationContent.keepSynced(true);
                currentMessages = currentConversationContent.child("messages");
                currentMessages.keepSynced(true);
                // load lần đầu tiên
                // thêm tin nhắn
                currentmessageChildEventListener = new ChildEventListener() {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String mContent = dataSnapshot.child("content").getValue().toString();
                        long mCreatedTime = Long.parseLong(dataSnapshot.child("createdTime").getValue().toString());
                        String mUserID = dataSnapshot.child("user_id").getValue().toString();
                        Bitmap bitmap = MemberAvatars.getInstant().getBitmap(mUserID);
                        Message m = new Message(mContent,mCreatedTime,mUserID);
                        ChatMessage chatMessage;
                        if(bitmap!=null){
                            chatMessage= new ChatMessage(FragmentChattingDetail.this.getActivity().getApplicationContext(),
                                    bitmap,m);
                        }
                        else{
                            chatMessage= new ChatMessage(FragmentChattingDetail.this.getActivity().getApplicationContext(),
                                    defaultBitmap,m);
                        }
                        chatContentLayoutContainer.addView(chatMessage);
                        // lăn thanh cuộn xuống dưới cùng
                        chatContentScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                chatContentScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
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
                currentMessages.orderByChild("createdTime").limitToLast(10)
                        .addChildEventListener(currentmessageChildEventListener);
                // gửi tin nhắn
                btn_send.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String messageContent = FragmentChattingDetail.this.edt_input_content.getText().toString();
                        if (messageContent != ""){
                            long time = System.currentTimeMillis();
                            String user_id = FragmentChattingDetail.this.userID;
                            Message sentMessage = new Message(messageContent, time, user_id);
                            currentMessages.push().setValue(sentMessage);
                            FragmentChattingDetail.this.edt_input_content.setText("");
                            currentConversationContent.child("lastMessage").setValue(messageContent);
                            currentConversationContent.child("lastUpdatedTime").setValue(time);
                        }
                    }
                });
            }
            // nếu là chat bình thường
            else{
                currentConversationContent = FirebaseDatabase.getInstance().getReference()
                        .child("conversations")
                        .child(parentFragment.getRecentConversationId());
                currentConversationContent.keepSynced(true);
                currentMessages = currentConversationContent.child("messages");
                currentMessages.keepSynced(true);
                // load lần đầu tiên
                // thêm tin nhắn
                currentmessageChildEventListener = new ChildEventListener() {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String mContent = dataSnapshot.child("content").getValue().toString();
                        long mCreatedTime = Long.parseLong(dataSnapshot.child("createdTime").getValue().toString());
                        String mUserID = dataSnapshot.child("user_id").getValue().toString();
                        Bitmap bitmap = MemberAvatars.getInstant().getBitmap(mUserID);
                        Message m = new Message(mContent,mCreatedTime,mUserID);
                        ChatMessage chatMessage;
                        if(bitmap!=null){
                            chatMessage= new ChatMessage(FragmentChattingDetail.this.getActivity().getApplicationContext(),
                                    bitmap,m);
                        }
                        else{
                            chatMessage = new ChatMessage(FragmentChattingDetail.this.getActivity().getApplicationContext(),
                                    defaultBitmap,m);
                        }

                        chatContentLayoutContainer.addView(chatMessage);
                        // lăn thanh cuộn xuống dưới cùng
                        chatContentScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                chatContentScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
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
                currentMessages.orderByChild("createdTime").limitToLast(15).addChildEventListener(currentmessageChildEventListener);
                // gửi tin nhắn
                btn_send.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String messageContent = FragmentChattingDetail.this.edt_input_content.getText().toString();
                        if (messageContent != "" && messageContent != null){
                            long time = System.currentTimeMillis();
                            String user_id = FragmentChattingDetail.this.userID;
                            Message sentMessage = new Message(messageContent, time, user_id);
                            currentMessages.push().setValue(sentMessage);
                            FragmentChattingDetail.this.edt_input_content.setText("");
                            currentConversationContent.child("lastMessage").setValue(messageContent);
                            currentConversationContent.child("lastUpdatedTime").setValue(time);
                        }
                    }
                });

            }

        }
        return view;
    }
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v,savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // xóa event listener của child đi, tránh lỗi phải lắng nghe quá nhiều khiến máy bị đứng
        if (currentMessages!=null&& currentmessageChildEventListener!=null)
            currentMessages.removeEventListener(currentmessageChildEventListener);
    }

    public void setParentFragment(FragmentChatting fragment){
        parentFragment = fragment;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }
}
