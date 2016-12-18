package com.uitstu.party.supports;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.uitstu.party.models.Message;

/**
 * Created by duy tung dao on 12/14/2016.
 */

public class ChatMessage extends LinearLayout {
    private String currentUserID;
    private RoundedImageView userAvatar;
    private FrameLayout chatContentContainer;
    private TextView chatContent;
    private Message message;

    public ChatMessage(Context context) {
        super(context);

    }
    public ChatMessage(Context context,Bitmap avatar,Message mess) {
        super(context);
        this.message = mess;
        this.currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LayoutParams thisParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(thisParams);
        this.setPadding(8,8,8,8);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        userAvatar = new RoundedImageView(context);
        userAvatar.setImageBitmap(RoundedImageView.getCroppedBitmap(avatar,100));
        userAvatar.setLayoutParams(params);


        chatContent = new TextView(context);
        chatContent.setText(message.getContent());
        chatContent.setMaxWidth(1000);
        chatContent.setLayoutParams(params);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        chatContentContainer = new FrameLayout(context);
        chatContentContainer.setPadding(16,16,16,16);
        chatContentContainer.addView(chatContent);
        chatContentContainer.setLayoutParams(params);
        // nếu là user hiện tại: thêm content -> avatar, đặt bên phải
        if(message.getUser_id().equals(currentUserID)){
            this.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
            chatContentContainer.setBackgroundColor(Color.BLUE);
            this.addView(chatContentContainer);
            this.addView(userAvatar);
        }
        // nếu không phải user hiện tại: thêm avatar->content, đặt ben
        else {
            this.setGravity(Gravity.BOTTOM|Gravity.LEFT);
            chatContentContainer.setBackgroundColor(Color.GREEN);
            this.addView(userAvatar);
            this.addView(chatContentContainer);
        }
    }
}
