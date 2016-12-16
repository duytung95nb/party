package com.uitstu.party.AsyncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.fragments.FragmentChattingList;
import com.uitstu.party.models.Conversation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by duy tung dao on 12/14/2016.
 */

public class AsyncLoadChatItem extends AsyncTask<Set<Conversation>,Integer,Set<Conversation>> {
    private FragmentChattingList fragmentChattingList;
    private DatabaseReference currentuserConversations;

    private Set<Conversation> currentConversations;
    public AsyncLoadChatItem() {
        super();
    }
    public AsyncLoadChatItem(FragmentChattingList fragment) {
        super();
        fragmentChattingList = fragment;
        currentConversations = new HashSet<>();
    }
    public final Semaphore semaphore = new Semaphore(0);

    @Override
    protected Set<Conversation> doInBackground(Set<Conversation>... params) {
        if(isNetworkAvailable()) {
            // lấy dữ liệu về set
            final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (currentUserID != null) {
                currentuserConversations = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(currentUserID)
                        .child("conversations");
            }
            if(currentuserConversations!=null){
                currentuserConversations.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentConversations.clear();
                        // giống như lấy dữ liệu trong sqlite java
                        for (DataSnapshot snapShot: dataSnapshot.getChildren()) {
                            String s = snapShot.getValue().toString();
                            Conversation c = new Conversation(s);
                            currentConversations.add(c);
                        }
                        semaphore.release();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return currentConversations;
    }

    // đã hoàn thành lấy danh sách conversation từ firebase
    @Override
    protected void onPostExecute(Set<Conversation> set) {
        super.onPostExecute(set);
        fragmentChattingList.loadChatItemsToLayout(set);
    }
    // kiểm tra xem có mạng không
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)fragmentChattingList.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
        // nếu 3 điều kiện != null, available, isconnected đều đúng
        return networkInfo!=null&&networkInfo.isAvailable()&&networkInfo.isConnected();
    }
}
