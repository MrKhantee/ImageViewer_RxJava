package com.example.n5050.imageviewerrxjava;

import retrofit2.http.GET;

/**
 * Created by n5050 on 11/11/2017.
 */

public interface ImagesUriApi {

    @GET("/tutorial/jsonparsetutorial.txt")
    io.reactivex.Observable<Country> fetchCountryData();


}
