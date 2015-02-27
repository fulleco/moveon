package com.application.moveon.rest;
import com.application.moveon.model.MessagePojo;
import com.application.moveon.rest.callback.AddFriend_Callback;
import com.application.moveon.rest.callback.AddMessage_Callback;
import com.application.moveon.rest.callback.AnswerDemand_Callback;
import com.application.moveon.rest.callback.CreateCircle_Callback;
import com.application.moveon.rest.callback.EditUser_Callback;
import com.application.moveon.rest.callback.GetCercles_Callback;
import com.application.moveon.rest.callback.GetMessage_Callback;
import com.application.moveon.rest.callback.GetParticipants_Callback;
import com.application.moveon.rest.callback.Register_Callback;
import com.application.moveon.rest.callback.UpdatePosition_Callback;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.DemandsPojo;
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

    @GET("/get_cercles")
    void getCercles(@Query("login") String id, Callback<CerclePojo[]> cb);

    @GET("/get_allparticipants")
    void getAllParticipants(@Query("circles") String circles, Callback<UserPojo[]> cb);

    @GET("/get_allmessages")
    void getAllMessages(@Query("id_receiver") String id_user, Callback<MessagePojo[]> cb);

    @GET("/get_participants")
    void getParticipants(@Query("id_cercle") String id, GetParticipants_Callback cb);

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

    @GET("/get_user")
    void addfriendtodb(@Query("login") String login, Callback<UserPojo> cb);

    @FormUrlEncoded
    @POST("/update_user")
    void updateuser(@Field("firstname") String firstname,
                    @Field("lastname") String lastname,
                    @Field("password") String password,
                    @Field("email") String email,
                    @Field("id") String id, EditUser_Callback cb);

    @FormUrlEncoded
    @POST("/update_position")
    void updateuser(
            @Field("id") String id,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            UpdatePosition_Callback cb);

    @FormUrlEncoded
    @POST("/add_friend")
    void addfriend(@Field("sender")String sender,
                   @Field("receiver") String receiver,
                   AddFriend_Callback cb);

    @GET("/get_friends")
    void getfriends(@Query("login") String mail,
                    Callback<UserPojo[]> cb);

    @GET("/get_demands")
    void getdemands(@Query("login") String mail,
                    Callback<DemandsPojo[]> cb);
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
                      @Field("users") String hosts,
                      @Field("date_debut") String date,
                      @Field("date_fin") String date_fin,
                      @Field("latitude") double latitude,
                      @Field("longitude") double longitude,
                      @Field("rayon") int rayon, CreateCircle_Callback cb);

    @FormUrlEncoded
    @POST("/add_message")
    void addMessage(@Field("id_circle") String idCircle,
                    @Field("id_sender") String idSender,
                    @Field("id_receiver") String idReceiver,
                    @Field("content") String content,
                    @Field("date") String date,
                    @Field("seen") int seen,
                    AddMessage_Callback c);

    @GET("/get_messages")
    void getmessages(@Query("id_receiver") String idReceiver,
                     GetMessage_Callback cb);


}
