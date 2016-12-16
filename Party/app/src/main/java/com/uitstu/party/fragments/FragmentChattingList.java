package com.uitstu.party.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.uitstu.party.AsyncTasks.AsyncLoadChatItem;
import com.uitstu.party.R;
import com.uitstu.party.models.Conversation;
import com.uitstu.party.supports.ChatItem;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by duy tung dao on 12/10/2016.
 */

public class FragmentChattingList extends Fragment {
    private LinearLayout linearLayout;
    private FragmentChatting parentFragment;

    private AsyncLoadChatItem asyncLoadChatItem;

    // Khởi tạo asynctask
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asyncLoadChatItem = new AsyncLoadChatItem(this);
    }
    // chạy asynctask trong sự kiện hoàn thành hết giao diện
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_list, container, false);

        // get layout by id from root view
        linearLayout = (LinearLayout) view.findViewById(R.id.fragment_chatting_list_layout);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener(){
            private boolean gotLayoutWidth = false; // đã lấy được chiều rộng layout chưa?
            // hoàn thành layout rồi và các view đều đã được biết hết
            @Override
            public void onGlobalLayout(){
                if(gotLayoutWidth==false){
                    asyncLoadChatItem.execute();
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
    public void loadChatItemsToLayout(Set<Conversation> conversationsSet){
        // List các group chat ra đây
        if(conversationsSet!=null){
            // clear hết view trong đó
            linearLayout.removeAllViews();
            // với mõi chuỗi i trong set thêm 1 chat item
            for (Conversation i: conversationsSet) {

                Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                ChatItem chatItem = new ChatItem(FragmentChattingList.this.getContext(),b, i,linearLayout.getWidth());
                chatItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(parentFragment!=null){
                            // send value
                            parentFragment.setRecentChatID("this is a test!");
                            Log.d("recent chat: ",parentFragment.getRecentChatID());
                            parentFragment.switchToNextFragment();
                        }
                    }
                });
                linearLayout.addView(chatItem);
            }

        }
    }
    public void setParentFragment(FragmentChatting fragment){
        parentFragment = fragment;
    }
}
