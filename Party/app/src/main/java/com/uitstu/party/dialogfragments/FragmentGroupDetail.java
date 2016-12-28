package com.uitstu.party.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uitstu.party.R;
import com.uitstu.party.models.User;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.supports.GroupChatDialogItem;
import com.uitstu.party.supports.MemberAvatars;

/**
 * Created by duy tung dao on 12/28/2016.
 */

public class FragmentGroupDetail extends DialogFragment {

    private LinearLayout list_people_container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_fragment_group_detail, null);

        list_people_container = (LinearLayout)rootView.findViewById(R.id.list_people_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for (User u: PartyFirebase.users) {
            Bitmap avatar =  MemberAvatars.getInstant().getBitmap(u.UID);
            GroupChatDialogItem groupChatDialogItem = new GroupChatDialogItem(
                    this.getActivity().getApplicationContext(),
                    avatar,
                    u);
            list_people_container.addView(groupChatDialogItem);
        }
        builder.setTitle("Group Members");
        builder.setView(rootView);

        builder.setPositiveButton("Personal Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
