package com.application.moveon.rest;
import com.application.moveon.rest.modele.User;
import com.squareup.okhttp.Call;

import org.json.JSONObject;



import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Created by Hugo on 28/11/2014.
 */
public interface MoveOnService {

    @GET("/account_existstest")
    void user(@Query("login") String id, Callback<Boolean> cb);

    @POST("/add_usertest")
    void adduser(@Query("firstName") String firstname,
                 @Query("lastName") String lastname,
                 @Query("login") String login,
                 @Query("password") String password,
                 @Query("imageprofile") String imageprofile, Callback<Boolean> cb);

    @GET("/select_usertest")
    void selectuser(@Query("login") String login,
                    @Query("password") String password, Callback<User> cb);

    @FormUrlEncoded
    @POST("/update_usertest")
    void updateuser(@Field("firstname") String firstname,
                    @Field("lastname") String lastname,
                    @Field("id") String id, Callback<String> cb);

}
