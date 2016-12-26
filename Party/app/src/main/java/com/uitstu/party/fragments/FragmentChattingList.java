package com.uitstu.party.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.MainActivity;
import com.uitstu.party.R;
import com.uitstu.party.models.Conversation;
import com.uitstu.party.models.RecentGroupConversation;
import com.uitstu.party.supports.ChatItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by duy tung dao on 12/10/2016.
 */

public class FragmentChattingList extends Fragment {
    private LinearLayout linearLayout;
    private LinearLayout main_chatitem_container;
    private FragmentChatting parentFragment;

    private String currentGroupID;
    private DatabaseReference currentgroupConversation;
    private DatabaseReference currentuserConversations;
    private Set<Conversation> currentConversations;         // set of data objects
    private ValueEventListener currentValueEventGroupConversation;
    private ValueEventListener currentValueEventUserConversations;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_list, container, false);

        // get layout by id from root view
        linearLayout = (LinearLayout) view.findViewById(R.id.fragment_chatting_list_layout);
        main_chatitem_container = (LinearLayout)view.findViewById(R.id.main_chatitem_container);
        // lấy dữ liệu về set
        currentConversations = new HashSet<>();
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserID != null) {
            currentuserConversations = FirebaseDatabase.getInstance().getReference().child("conversationInUser")
                    .child(currentUserID)
                    .child("conversations");
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener(){
            private boolean gotLayoutWidth = false; // đã lấy được chiều rộng layout chưa?
            private Conversation c;
            private RecentGroupConversation recentGroupConversation;
            // hoàn thành layout rồi và các view đều đã được biết hết
            @Override
            public void onGlobalLayout(){
                if(gotLayoutWidth==false){
                    if(isNetworkAvailable()) {
                        // load ra group conversation trước
                        // lấy id của party hiện tại
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(currentUserID)
                                .child("curPartyID").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentGroupID = dataSnapshot.getValue().toString();
                                if (currentGroupID != null) {
                                    currentgroupConversation = FirebaseDatabase.getInstance().getReference()
                                            .child("parties")
                                            .child(currentGroupID)
                                            .child("conversation");
                                    currentgroupConversation.keepSynced(true);
                                }

                                currentValueEventGroupConversation = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // duyệt
                                        if(dataSnapshot.hasChildren()){
                                            main_chatitem_container.removeAllViews();
                                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                            ChatItem chatItem = new ChatItem(FragmentChattingList.this.getContext(),b,
                                                    main_chatitem_container.getWidth());
                                            chatItem.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(parentFragment!=null){
                                                        // set conversation id trước khi thêm detail fragments
                                                        parentFragment.setRecentGroupID(recentGroupConversation.getGroupID());
                                                        parentFragment.addRecentGroupChatDetailFragment();
                                                        parentFragment.switchToNextFragment();
                                                    }
                                                }
                                            });
                                            recentGroupConversation = new RecentGroupConversation(currentGroupID,chatItem);
                                            main_chatitem_container.addView(chatItem);  // add to view
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                currentgroupConversation.addValueEventListener(currentValueEventGroupConversation);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        // add những conversation cá nhân
                        if(currentuserConversations!=null){
                            currentValueEventUserConversations = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentConversations.clear();
                                    linearLayout.removeAllViews();
                                    // duyệt từng key conversation id trong bảng conversation in user
                                    for (DataSnapshot snapShot: dataSnapshot.getChildren()) {
                                        String conversationID = snapShot.getValue().toString();
                                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                        ChatItem chatItem = new ChatItem(FragmentChattingList.this.getContext(),b,
                                                linearLayout.getWidth());
                                        // conversation c điều khiển chat item (thêm sự kiện click cho chat item)
                                        c = new Conversation(conversationID,chatItem);
                                        chatItem.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(parentFragment!=null){
                                                    // set conversation id trước khi thêm detail fragments
                                                    parentFragment.setRecentConversationId(c.getConversationID());
                                                    parentFragment.addNormalChatDetailFragment();
                                                    parentFragment.switchToNextFragment();
                                                }
                                            }
                                        });
                                        currentConversations.add(c);    // add to list
                                        linearLayout.addView(chatItem);  // add to view
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            currentuserConversations.addValueEventListener(currentValueEventUserConversations);
                        }
                    }
                    gotLayoutWidth = true;
                }
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }

    @Override
    public void onPause() {
        if(currentValueEventUserConversations != null)
            Log.d(this.getClass().getName()+" Listener still alive"," listening");
        Log.d(this.getClass().getName()," paused");
        removeEventListener();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getName()," destroyed");
        removeEventListener();
        if(currentValueEventUserConversations != null)
            Log.d(this.getClass().getName()+" Listener still alive"," listening");
    }

    public void setParentFragment(FragmentChatting fragment){
        parentFragment = fragment;
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
        // nếu 3 điều kiện != null, available, isconnected đều đúng
        return networkInfo!=null&&networkInfo.isAvailable()&&networkInfo.isConnected();
    }
    // Xóa event của fragment này
    private void removeEventListener(){
        if(currentuserConversations!=null&&currentValueEventUserConversations!=null)
            currentuserConversations.removeEventListener(currentValueEventUserConversations);
        if(currentgroupConversation!=null&&currentValueEventGroupConversation!=null){
            currentgroupConversation.removeEventListener(currentValueEventGroupConversation);
        }
    }

}
