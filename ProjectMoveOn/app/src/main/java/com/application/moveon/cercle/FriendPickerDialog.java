package com.application.moveon.cercle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.application.moveon.R;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschené on 18/02/2015.
 */
public class FriendPickerDialog extends DialogFragment {
    private ArrayList<UserPojo> mFriendsList;
    private ArrayList<String> mSelectedItems;
    NoticeDialogListener mListener;




    public static FriendPickerDialog newInstance(CharSequence[] array) {
        FriendPickerDialog f = new FriendPickerDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putCharSequenceArray("values", array);
        f.setArguments(args);

        return f;
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CharSequence[] array =  getArguments().getCharSequenceArray("values");
        mSelectedItems = new ArrayList<String>();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title

        builder.setTitle(R.string.friend_picker_title).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //TODO



            }
        }) .setMultiChoiceItems(array, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            //TODO
                            //Get le UserPojo qui correspond a cet index

                            mSelectedItems.add(array[which].toString());
                        } else if (mSelectedItems.contains(array[which].toString())) {
                            // Else, if the item is already in the array, remove it
                            mSelectedItems.remove(array[which].toString());
                        }
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                FragmentCreateCercle fcc = (FragmentCreateCercle)getTargetFragment();
                fcc.setUsers(mSelectedItems);

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        // ne rien faire ?

                    }
                });



        return builder.create();
    }

}
