package com.hust.emotion.datasource;

import com.hust.emotion.api.RecognizeApi;
import com.hust.emotion.api.RestApiAdapter;
import com.hust.emotion.model.RecognizeResult;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yongshan on 3/24/17.
 */

public class RecognizeRepo {
    public void recognize(String path, final RecognizeReposity.RecognizeCallBack callBack) {
        File image = new File(path);
        if (image.exists() && image.isFile()) {
            RequestBody body = RequestBody.create(MediaType.parse("image"), image);
            RestApiAdapter.createService(RecognizeApi.class)
                    .recognize(MultipartBody.Part.createFormData("file", image.getName(), body))
                    .enqueue(new Callback<RecognizeResult>() {
                        @Override
                        public void onResponse(Call<RecognizeResult> call, Response<RecognizeResult> response) {
                            if (response.isSuccessful()) {
                                callBack.onSucccess(response.body());
                            } else {
                                callBack.onFailure();
                            }
                        }

                        @Override
                        public void onFailure(Call<RecognizeResult> call, Throwable t) {
                            t.printStackTrace();
                            callBack.onFailure();
                        }
                    });
        }
    }
}
