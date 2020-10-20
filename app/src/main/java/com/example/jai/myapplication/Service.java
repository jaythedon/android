package com.example.jai.myapplication;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by jai on 10/15/2020.
 */

public interface Service {

    @Multipart
    @POST("/")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image);
}
