package com.application.moveon.rest;
import com.application.moveon.rest.callback.AddFriend_Callback;
import com.application.moveon.rest.callback.AnswerDemand_Callback;
import com.application.moveon.rest.callback.CreateCircle_Callback;
import com.application.moveon.rest.callback.EditUser_Callback;
import com.application.moveon.rest.callback.GetDemands_Callback;
import com.application.moveon.rest.callback.GetFriends_Callback;
import com.application.moveon.rest.callback.Register_Callback;
import com.application.moveon.rest.modele.UserPojo;


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

    @GET("/account_exists")
    void userexists(@Query("login") String id, Register_Callback cb);

    @POST("/add_user")
    int adduser(@Query("firstName") String firstname,
                 @Query("lastName") String lastname,
                 @Query("login") String login,
                 @Query("password") String password,
                 @Query("imageprofile") String imageprofile);

    @GET("/select_user")
    void selectuser(@Query("login") String login,
                    @Query("password") String password, Callback<UserPojo> cb);

    @FormUrlEncoded
    @POST("/update_user")
    void updateuser(@Field("firstname") String firstname,
                    @Field("lastname") String lastname,
                    @Field("password") String password,
                    @Field("email") String email,
                    @Field("id") String id, EditUser_Callback cb);

    @FormUrlEncoded
    @POST("/add_friend")
    void addfriend(@Field("sender")String sender,
                   @Field("receiver") String receiver,
                   AddFriend_Callback cb);

    @GET("/get_friends")
    void getfriends(@Query("login") String mail,
                    GetFriends_Callback cb);

    @GET("/get_demands")
    void getdemands(@Query("login") String mail,
                    GetDemands_Callback cb);
    @FormUrlEncoded
    @POST("/answer_demand")
    void answerdemand(@Field("sender")String sender,
                      @Field("receiver") String receiver,
                      @Field("accept") boolean accept,
                      AnswerDemand_Callback cb
                      );

    @FormUrlEncoded
    @POST("/create_circle")
    void createcircle(@Field("title") String title,
                      @Field("creator")String creator,
                      @Field("users") UserPojo[] host,
                      @Field("date_debut") String date,
                      @Field("date_fin") String date_fin,
                      @Field("latitude") float latitude,
                      @Field("longitude") float longitude,
                      @Field("rayon") int rayon, CreateCircle_Callback cb);

}
