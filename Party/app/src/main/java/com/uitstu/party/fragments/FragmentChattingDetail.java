package com.uitstu.party.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.uitstu.party.dialogfragments.FragmentCreateParty;
import com.uitstu.party.dialogfragments.FragmentGroupDetail;
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
    private ImageButton btn_menu_show;
    private FloatingActionButton btn_back;
    private FloatingActionButton btn_group_detail;
    private FragmentChatting parentFragment;
    private RelativeLayout fragment_chatting_detail_layout;

    private DatabaseReference currentConversationContent;           // chứa nội dung các conversation
    private DatabaseReference currentMessages;                      // chứa nội dung các message
    private ChildEventListener currentmessageChildEventListener;    // bộ lắng nghe sự kiện child của message
    private DatabaseReference currentLoadMoreMessages;              // chứa nội dung các message
    private ChildEventListener currentLoadMoreMessagesListener;              // chứa nội dung các message
    private boolean isGroupChat;
    private final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private int currentMessagesAmount;                               // số message được load
    private boolean isWatchingMessageHistory;                        // đang xem history message
    private Message firstMessageInList;                              // tin nhắn đầu tiên trong danh sách (luôn cập nhật)
    //private Message firstMessageAfterDone;                           // tin nhắn đầu tiên trong danh sách (khi không còn cập nhật nữa)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_detail, container, false);
        currentMessagesAmount = 16;
        // cố định cho cả normal chat và group chat
        fragment_chatting_detail_layout = (RelativeLayout) view.findViewById(R.id.fragment_chatting_detail_layout);
        chatContentScrollView = (ScrollView) view.findViewById(R.id.chatContentScrollView);
        // lăn thanh cuộn xuống dưới cùng
        chatContentScrollView.post(new Runnable() {
            @Override
            public void run() {
                chatContentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        chatContentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // lấy vị trí của thanh cuộn mỗi khi kéo lên trên cùng
                float verticalScrollBarPosition =  chatContentScrollView.getScrollY();
                // đang ở trên cùng của thanh chat
                Log.d("Scroll amount: ",chatContentScrollView.getMaxScrollAmount()+"" );
                if(verticalScrollBarPosition==0){
                    if(currentLoadMoreMessages!=null){
                        if(chatContentLayoutContainer.getChildAt(0)!=null){
                            isWatchingMessageHistory = true;
                            // lấy message đầu tiên của list
                            if(chatContentLayoutContainer.getChildAt(0) instanceof ChatMessage){
                                firstMessageInList = ((ChatMessage) chatContentLayoutContainer.getChildAt(
                                        0)).message;
                                currentLoadMoreMessagesListener = new ChildEventListener() {
                                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                                    int i=0;
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        // datasnapshot tượng trưng cho 1 message
                                        if(!dataSnapshot.getKey().equals(firstMessageInList.getM_id())){
                                            String mID = dataSnapshot.getKey();
                                            String mContent = dataSnapshot.child("content").getValue().toString();
                                            long mCreatedTime = Long.parseLong(dataSnapshot.child("createdTime").getValue().toString());
                                            String mUserID = dataSnapshot.child("user_id").getValue().toString();
                                            Bitmap bitmap = MemberAvatars.getInstant().getBitmap(mUserID);
                                            Message m = new Message(mID,mContent,mCreatedTime,mUserID);
                                            ChatMessage chatMessage;
                                            if(bitmap!=null){
                                                chatMessage= new ChatMessage(FragmentChattingDetail.this.getActivity().getApplicationContext(),
                                                        bitmap,m);
                                            }
                                            else{
                                                chatMessage= new ChatMessage(FragmentChattingDetail.this.getActivity().getApplicationContext(),
                                                        defaultBitmap,m);
                                            }
                                            chatContentLayoutContainer.addView(chatMessage,i++);
                                        }
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
                                currentLoadMoreMessages.removeEventListener(currentLoadMoreMessagesListener);
                                currentLoadMoreMessages.orderByChild("createdTime")
                                        .endAt(firstMessageInList.getCreatedTime())
                                        .limitToLast(currentMessagesAmount)
                                        .addChildEventListener(currentLoadMoreMessagesListener);
                            }
                        }

                    }

                }
                // nếu vị trí scroll trong khoảng từ 4/5 max scroll tới max scroll tới  thì đang ở bottom
                else if(verticalScrollBarPosition<chatContentScrollView.getMaxScrollAmount()
                        &&verticalScrollBarPosition>4*chatContentScrollView.getMaxScrollAmount()/5){
                    Log.d("verticalScrollBa ","at bottom");
                    isWatchingMessageHistory = false;
                    // xóa listener
                    if(currentLoadMoreMessagesListener!=null&&currentLoadMoreMessages!=null){
                        currentLoadMoreMessages.removeEventListener(currentLoadMoreMessagesListener);
                    }
                }
                // đang ở dưới cùng của thanh chat


            }
        });
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
        btn_menu_show = (ImageButton) view.findViewById(R.id.btn_menu_show);
        btn_menu_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_back.getVisibility()== View.VISIBLE&&btn_group_detail.getVisibility() == View.VISIBLE){
                    btn_back.setVisibility(View.GONE);
                    btn_group_detail.setVisibility(View.GONE);
                }
                else{
                    btn_back.setVisibility(View.VISIBLE);
                    btn_group_detail.setVisibility(View.VISIBLE);
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
        btn_group_detail = (FloatingActionButton) view.findViewById(R.id.btn_group_detail);
        btn_group_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new FragmentGroupDetail();
                //dialog.setTargetFragment(this, REQUEST_CODE);
                dialog.show(getChildFragmentManager(),"Group Detail");
            }
        });
        btn_send = (Button) view.findViewById(R.id.btn_send);


        if(parentFragment!=null){
            // không cố định
            // nếu là group chat
            if(isGroupChat){
                // lấy mã user -> lấy mã party -> lấy messages trong conversation party

                currentConversationContent = FirebaseDatabase.getInstance().getReference()
                        .child("parties")
                        .child(PartyFirebase.user.curPartyID)
                        .child("conversation");
                currentMessages = currentConversationContent.child("messages");
                currentLoadMoreMessages = currentConversationContent.child("messages");
                // thêm tin nhắn
                currentmessageChildEventListener = new ChildEventListener() {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String mID = dataSnapshot.getKey();
                        String mContent = dataSnapshot.child("content").getValue().toString();
                        long mCreatedTime = Long.parseLong(dataSnapshot.child("createdTime").getValue().toString());
                        String mUserID = dataSnapshot.child("user_id").getValue().toString();
                        Bitmap bitmap = MemberAvatars.getInstant().getBitmap(mUserID);
                        Message m = new Message(mID,mContent,mCreatedTime,mUserID);
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
                        // nếu không phải xem lịch sử tin nhắn thì kéo xuống cuối
                        if(!isWatchingMessageHistory){
                            // lăn thanh cuộn xuống dưới cùng
                            chatContentScrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    chatContentScrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }

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
                currentMessages.orderByChild("createdTime").limitToLast(currentMessagesAmount)
                        .addChildEventListener(currentmessageChildEventListener);

                // gửi tin nhắn
                btn_send.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String messageContent = FragmentChattingDetail.this.edt_input_content.getText().toString();
                        if (messageContent != ""){
                            long time = System.currentTimeMillis();
                            String user_id = FragmentChattingDetail.this.userID;
                            currentMessages.push().setValue(new Message(currentMessages.push().getKey(),
                                    messageContent,
                                    time,
                                    user_id));
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
                        String mID = dataSnapshot.getKey();
                        String mContent = dataSnapshot.child("content").getValue().toString();
                        long mCreatedTime = Long.parseLong(dataSnapshot.child("createdTime").getValue().toString());
                        String mUserID = dataSnapshot.child("user_id").getValue().toString();
                        Bitmap bitmap = MemberAvatars.getInstant().getBitmap(mUserID);
                        Message m = new Message(mID,mContent,mCreatedTime,mUserID);
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
                            currentMessages.push().setValue(new Message(currentMessages.push().getKey(),
                                    messageContent,
                                    time,
                                    user_id));
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
        // xóa event listener của child đi
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
