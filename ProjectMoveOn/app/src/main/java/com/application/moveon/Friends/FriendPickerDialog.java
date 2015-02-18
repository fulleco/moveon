package com.application.moveon.friends;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.application.moveon.R;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;

/**
 * Created by Quentin Bitschené on 18/02/2015.
 */
public class FriendPickerDialog extends DialogFragment {
    private ArrayList<UserPojo> mFriendsList;
    //TODO au lieu d'integer utiliser le userpojo
    //private ArrayList<UserPojo> mSelectedItems;
    private ArrayList<Integer> mSelectedItems;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.friend_picker_title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                //TODO
                // passer en param la liste d'amis de l'utilisateur
                //.setMultiChoiceItems(mFriendsList, null,
                .setMultiChoiceItems(R.array.test_friend_picker, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    //TODO
                                    //Get le UserPojo qui correspond a cet index

                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        // ne rien faire ?

                    }
                });

        return builder.create();
    }
}
