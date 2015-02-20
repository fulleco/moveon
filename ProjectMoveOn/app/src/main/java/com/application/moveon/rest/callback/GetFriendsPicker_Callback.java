package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.application.moveon.cercle.FragmentCreateCercle;
import com.application.moveon.cercle.FriendPickerDialog;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 13/01/2015.
 */
public class GetFriendsPicker_Callback implements Callback<UserPojo[]> {

    private String id;
    private Activity activity;
    private ArrayList<Integer> mSelectedItems;
    private Button b;
    private FragmentCreateCercle fcc;
    FragmentManager fm;

    public GetFriendsPicker_Callback(Activity activity, Button b, FragmentCreateCercle fcc, FragmentManager fm) {
        this.activity = activity;
        this.b = b;
        this.fcc = fcc;
        this.fm = fm;
    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {

        if(userPojos == null) return;
        ArrayList<UserPojo> datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));
        ArrayList<CharSequence> ids = new ArrayList<CharSequence>();
        for(UserPojo up : datas){
            ids.add(up.getLogin());
        }

        CharSequence[] filled = new CharSequence[0];
        final CharSequence[] array = ids.toArray(filled);
        Log.i("HUGO LOG", "Taille array : "+array.length);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FriendPickerDialog pickerDialog = FriendPickerDialog.newInstance(array);
                        pickerDialog.setTargetFragment(fcc,10);
                        pickerDialog.show(fm, "missiles");


                    }
                });
            }
        });






    }

    @Override
    public void failure(RetrofitError error) {

    }
}
