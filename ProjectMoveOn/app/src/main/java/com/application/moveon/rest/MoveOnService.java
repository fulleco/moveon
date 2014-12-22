package com.application.moveon.rest;
import com.application.moveon.rest.callback.Register_Callback;
import com.application.moveon.rest.modele.UserPojo;


import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Hugo on 28/11/2014.
 */
public interface MoveOnService {

    @GET("/account_exists")
    void userexists(@Query("login") String id, Register_Callback cb);

    @POST("/add_user")
    Boolean adduser(@Query("firstName") String firstname,
                 @Query("lastName") String lastname,
                 @Query("login") String login,
                 @Query("password") String password,
                 @Query("imageprofile") String imageprofile);

    @GET("/select_user")
    void selectuser(@Query("login") String login,
                    @Query("password") String password, Callback<UserPojo> cb);

    @FormUrlEncoded
    @POST("/update_usertest")
    void updateuser(@Field("firstname") String firstname,
                    @Field("lastname") String lastname,
                    @Field("id") String id, Callback<String> cb);

}
