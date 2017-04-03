package com.hust.emotion.api;

import com.hust.emotion.model.RecognizeResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by yongshan on 3/23/17.
 */

public interface RecognizeApi {
    @Multipart
    @POST("/recognize")
    Call<RecognizeResult> recognize(@Part MultipartBody.Part body);
}
