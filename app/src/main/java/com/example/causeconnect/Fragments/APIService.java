package com.example.causeconnect.Fragments;

import com.example.causeconnect.Notifications.MyResponse;
import com.example.causeconnect.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AIzaSyBEhR8GTZLvRXlOOJb3YnhVD1prCdVMMys" //Paste Authorization key here
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
