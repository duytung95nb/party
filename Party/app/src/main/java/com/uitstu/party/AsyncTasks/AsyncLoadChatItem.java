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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by duy tung dao on 12/14/2016.
 */

public class AsyncLoadChatItem extends AsyncTask<Set<String>,Integer,Set<String>> {
    private FragmentChattingList fragmentChattingList;
    private DatabaseReference currentuserConversations;
    Set<String> setOfConversationIDs;
    public AsyncLoadChatItem() {
        super();
    }
    public AsyncLoadChatItem(FragmentChattingList fragment) {
        super();
        fragmentChattingList = fragment;
        setOfConversationIDs = new HashSet<String>();

    }
    public final Semaphore semaphore = new Semaphore(0);

    @Override
    protected Set<String> doInBackground(Set<String>... params) {
        if(isNetworkAvailable()) {
            // lấy dữ liệu về set
            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("current userid: ", currentUserID);
            if (currentUserID != null) {
                currentuserConversations = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(currentUserID)
                        .child("conversations");
            }

            Log.d("convesations ", currentuserConversations.getKey());
            if(currentuserConversations!=null){
                currentuserConversations.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                        setOfConversationIDs.clear();
                        // giống như lấy dữ liệu trong sqlite java
                        for (DataSnapshot snapShot: dataSnapshot.getChildren()) {
                            String s = snapShot.getValue().toString();
                            setOfConversationIDs.add(s);
                        }
                        Log.d("Set size: ", setOfConversationIDs.size()+"");
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
        return setOfConversationIDs;
    }

    // đã hoàn thành lấy danh sách conversation id từ firebase
    @Override
    protected void onPostExecute(Set<String> set) {
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
