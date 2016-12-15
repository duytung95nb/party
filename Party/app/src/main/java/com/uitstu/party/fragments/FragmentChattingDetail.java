package com.uitstu.party.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.uitstu.party.R;
import com.uitstu.party.supports.ChatMessage;

/**
 * Created by duy tung dao on 12/10/2016.
 */

public class FragmentChattingDetail extends Fragment {
    private ScrollView chatContentScrollView;
    private LinearLayout chatContentLayoutContainer;
    private LinearLayout chatToolsBar;
    private EditText edt_input_content;
    private Button btn_send;
    private FragmentChatting parentFragment;
    private RelativeLayout fragment_chatting_detail_layout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_detail, container, false);
        fragment_chatting_detail_layout = (RelativeLayout) view.findViewById(R.id.fragment_chatting_detail_layout);
        chatContentScrollView = (ScrollView) view.findViewById(R.id.chatContentScrollView);
        chatContentLayoutContainer = (LinearLayout) view.findViewById(R.id.chatContentLayoutContainer);
        chatToolsBar = (LinearLayout)view.findViewById(R.id.chatToolsBar);
        edt_input_content = (EditText) view.findViewById(R.id.edt_input_content);
        btn_send = (Button) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        ChatMessage chatMessage = new ChatMessage(this.getActivity().getApplicationContext(),
                bitmap,"this is a really test content this is a really test content this is " +
                "a really test content this is a really test content",
                false);
        ChatMessage chatMessage1 = new ChatMessage(this.getActivity().getApplicationContext(),
                bitmap,"this is a really test content this is a really test content this is " +
                "a really test content this is a really test content",
                false);
        ChatMessage chatMessage2 = new ChatMessage(this.getActivity().getApplicationContext(),
                bitmap,"this is a really test content this is a really test content this is " +
                "a really test content this is a really test content",
                true);
        ChatMessage chatMessage3 = new ChatMessage(this.getActivity().getApplicationContext(),
                bitmap,"this is a really test content this is a really test content this is " +
                "a really test content this is a really test content",
                false);
        chatContentLayoutContainer.addView(chatMessage);
        chatContentLayoutContainer.addView(chatMessage1);
        chatContentLayoutContainer.addView(chatMessage2);
        chatContentLayoutContainer.addView(chatMessage3);
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
    public void setParentFragment(FragmentChatting fragment){
        parentFragment = fragment;
    }
}
