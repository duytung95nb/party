package com.uitstu.party.supports;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uitstu.party.R;
import com.uitstu.party.models.Conversation;

/**
 * Created by duy tung dao on 12/7/2016.
 */
/*Chiều rộng gấp 6 lần chiều ngang: avatar 1 phần, nội dung 4 phần, thời gian 1 phần*/
public class ChatItem extends LinearLayout{
    private RoundedImageView userAvatar;    // avatar
    private LinearLayout contentLayout;     // chứa 2 cái dưới
    private TextView groupName;             // hiển thị tên group
    private TextView lastMessage;           // hiển thị tin nhắn cuối cùng
    private TextView interactionTime;       // thời gian tương tác cuối cùng
    private Bitmap bmpAvatar;
    private Context thisContext;
    public ChatItem(Context context, Bitmap avatar, int parentWidth) {
        super(context);
        thisContext = context;

        bmpAvatar = avatar;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                parentWidth/6);
        this.setLayoutParams(layoutParams);
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setBackgroundColor(Color.WHITE);
        // user avatar
        userAvatar = new RoundedImageView(thisContext);
        userAvatar.setElevation(getResources().getDimension(R.dimen.avatar_elevation));
        //userAvatar.setLayoutParams(layoutParams);
        userAvatar.setImageBitmap(bmpAvatar);
        this.addView(userAvatar);
        // tên group
        LayoutParams contentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,1.0f);
        groupName = new TextView(thisContext);
        groupName = new TextView(thisContext,null, R.style.BoldFont);
        groupName.setGravity(Gravity.BOTTOM);
        groupName.setText("Party Name...");
        groupName.setLayoutParams(contentLayoutParams);
        //tin nhắn cuối cùng
        contentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,1.0f);
        lastMessage = new TextView(thisContext,null, R.style.FadedFont);
        lastMessage.setGravity(Gravity.TOP);
        lastMessage.setText("Last Message...");
        lastMessage.setLayoutParams(contentLayoutParams);
        // content
        contentLayoutParams = new LayoutParams(4*parentWidth/6,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentLayout = new LinearLayout(thisContext);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(contentLayoutParams);
        contentLayout.addView(groupName);
        contentLayout.addView(lastMessage);
        this.addView(contentLayout);

        // time
        contentLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        interactionTime = new TextView(thisContext);
        interactionTime.setGravity(Gravity.TOP);
        interactionTime.setText("Last Updated...");
        interactionTime.setLayoutParams(contentLayoutParams);
        this.addView(interactionTime);

    }
    public void RefreshContent(String groupname, String lastmessage, String interationtime){
        groupName.setText(groupname);
        lastMessage.setText(lastmessage);
        interactionTime.setText(interationtime);
    }
    // draw a bottom line
    @Override
    public void onDraw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(10);
        canvas.drawLine(0,this.getHeight(),this.getWidth(),this.getHeight(),p);
    }
}
