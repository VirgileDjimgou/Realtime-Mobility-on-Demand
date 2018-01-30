package com.bee.drive.gmail.network;

import java.util.List;

import com.bee.drive.gmail.model.Message;
import retrofit2.Call;
import retrofit2.http.GET;



public interface ApiInterface {
    @GET("inbox.json")
    Call<List<Message>> getInbox();
}
