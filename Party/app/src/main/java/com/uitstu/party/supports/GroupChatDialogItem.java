package com.uitstu.party.supports;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uitstu.party.models.User;

/**
 * Created by duy tung dao on 12/28/2016.
 */

public class GroupChatDialogItem extends LinearLayout{
    private CheckBox box;
    private RoundedImageView avatar;
    private TextView name;
    private TextView velocity;
    public User user;
    public GroupChatDialogItem(Context context,
                               Bitmap bitmapAvatar,
                               User u) {
        super(context);
        LayoutParams parentParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parentParams.setMargins(8,8,8,8);
        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(parentParams);
        this.setBackgroundColor(Color.GRAY);
        user = u;

        LayoutParams childParams = new LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childParams.gravity = Gravity.CENTER;
        childParams.weight= 1;
        box = new CheckBox(context);
        box.setLayoutParams(childParams);
        box.setGravity(Gravity.CENTER);

        childParams.weight= 1;
        avatar = new RoundedImageView(context);
        avatar.setImageBitmap(bitmapAvatar);
        avatar.setLayoutParams(childParams);

        childParams.weight= 2;
        name = new TextView(context);
        name.setText(u.name);
        name.setLayoutParams(childParams);

        childParams.weight= 2;
        velocity = new TextView(context);
        velocity.setText(u.maxVelocity.toString());
        velocity.setLayoutParams(childParams);

        this.addView(box);
        this.addView(avatar);
        this.addView(name);
        this.addView(velocity);

    }
    @Override
    public void onDraw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(30);
        canvas.drawLine(0,this.getHeight(),this.getWidth(),this.getHeight(),p);
    }
    public RoundedImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(RoundedImageView avatar) {
        this.avatar = avatar;
    }

    public CheckBox getBox() {
        return box;
    }

    public void setBox(CheckBox box) {
        this.box = box;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getVelocity() {
        return velocity;
    }

    public void setVelocity(TextView velocity) {
        this.velocity = velocity;
    }
}
