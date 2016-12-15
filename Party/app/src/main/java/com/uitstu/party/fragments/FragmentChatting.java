package com.uitstu.party.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uitstu.party.R;
import com.uitstu.party.adapters.AdapterViewPager;
import com.uitstu.party.supports.StaticViewPager;

import java.util.ArrayList;

/**
 * Created by Huy on 11/6/2016.
 */

public class FragmentChatting extends Fragment {
    private StaticViewPager chatViewPager;
    private ArrayList<Fragment> fragmentList;
    private FragmentChattingList fragmentChattingList;
    private FragmentChattingDetail fragmentChattingDetail;
    private static String recentChatID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v,savedInstanceState);

        // đưa danh sách fragment vào list
        fragmentList = new ArrayList<>();
        fragmentChattingList = new FragmentChattingList();
        fragmentChattingList.setParentFragment(this);
        fragmentChattingDetail = new FragmentChattingDetail();
        fragmentChattingDetail.setParentFragment(this);
        fragmentList.add(fragmentChattingList);
        fragmentList.add(fragmentChattingDetail);

        // gắn adapter cho view pager
        chatViewPager = (StaticViewPager) v.findViewById(R.id.chatViewPager);
        AdapterViewPager adapterViewPager = new AdapterViewPager(getChildFragmentManager(), fragmentList);
        chatViewPager.setAdapter(adapterViewPager);
    }
    public String getRecentChatID(){
        return recentChatID;
    }
    public void setRecentChatID(String value){
        recentChatID = value;
    }
    public void switchToNextFragment(){
        int currentItemIndex = chatViewPager.getCurrentItem();
        int maxIndex = fragmentList.size()-1;
        //  nếu index hiện tại nhỏ hơn index lớn nhất thì set next
        if(currentItemIndex==0)
            chatViewPager.setCurrentItem(1,true);
        if(currentItemIndex==1)
            chatViewPager.setCurrentItem(0,true);
        /*if(currentItemIndex<maxIndex)
            chatViewPager.setCurrentItem(currentItemIndex+1);*/
        // nếu bằng hoặc lớn hơn thì set về 0
        /*else
            chatViewPager.setCurrentItem(0);*/
    }
}
