package com.uitstu.party.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.uitstu.party.R;
import com.uitstu.party.models.Conversation;
import com.uitstu.party.supports.ChatItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by duy tung dao on 12/10/2016.
 */

public class FragmentChattingList extends Fragment {
    private LinearLayout linearLayout;
    private FragmentChatting parentFragment;

    private DatabaseReference currentuserConversations;
    private Set<Conversation> currentConversations;         // set of data objects
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // chạy asynctask trong sự kiện hoàn thành hết giao diện
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_list, container, false);

        // get layout by id from root view
        linearLayout = (LinearLayout) view.findViewById(R.id.fragment_chatting_list_layout);

        // lấy dữ liệu về set
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserID != null) {
            currentuserConversations = FirebaseDatabase.getInstance().getReference().child("conversationInUser")
                    .child(currentUserID)
                    .child("conversations");
            currentConversations = new HashSet<>();
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener(){
            private boolean gotLayoutWidth = false; // đã lấy được chiều rộng layout chưa?
            private Conversation c;
            // hoàn thành layout rồi và các view đều đã được biết hết
            @Override
            public void onGlobalLayout(){
                if(gotLayoutWidth==false){
                    if(isNetworkAvailable()) {

                        if(currentuserConversations!=null){
                            currentuserConversations.addValueEventListener(new ValueEventListener() {
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
                                                ChatItem chat = (ChatItem) v;
                                                if(parentFragment!=null){
                                                    // set conversation id trước khi thêm detail fragments
                                                    parentFragment.setRecentConversationId(c.getConversationID());
                                                    parentFragment.addDetailFragment();
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
                            });
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

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
        // nếu 3 điều kiện != null, available, isconnected đều đúng
        return networkInfo!=null&&networkInfo.isAvailable()&&networkInfo.isConnected();
    }
    public void setParentFragment(FragmentChatting fragment){
        parentFragment = fragment;
    }
}
