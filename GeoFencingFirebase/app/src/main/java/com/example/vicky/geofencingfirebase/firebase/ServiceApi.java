package com.example.vicky.geofencingfirebase.firebase;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {

    @POST("/fcm/send")
    Call<Mensaje> create(@Body Mensaje mensaje);

}
