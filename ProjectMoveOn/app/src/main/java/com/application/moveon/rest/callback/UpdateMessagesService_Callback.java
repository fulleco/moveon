package com.application.moveon.rest.callback;

import com.application.moveon.model.MessagePojo;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 26/02/2015.
 */
public class UpdateMessagesService_Callback implements Callback<MessagePojo[]> {

    private MoveOnDB db;

    public UpdateMessagesService_Callback(MoveOnDB db){
        this.db = db;
    }

    @Override
    public void success(MessagePojo[] messagePojos, Response response) {

        ArrayList<MessagePojo> datas;

        if(messagePojos == null) {
            datas =new ArrayList<MessagePojo>();
        }else{
            datas = new ArrayList<MessagePojo>(Arrays.asList(messagePojos));
        }
        db.open();
        db.updateMessages(datas);
        db.close();
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
