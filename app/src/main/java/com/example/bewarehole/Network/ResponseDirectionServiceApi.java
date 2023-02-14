package com.example.bewarehole.Network;

import android.database.Observable;

import com.example.bewarehole.Model.Response;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ResponseDirectionServiceApi {
    @GET("api/directions/json?")
    Observable<Response> getdirections(@Query("s")String s);

}
