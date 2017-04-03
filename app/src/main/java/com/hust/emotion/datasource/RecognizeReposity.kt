package com.hust.emotion.datasource

import com.hust.emotion.api.RecognizeApi
import com.hust.emotion.api.RestApiAdapter
import com.hust.emotion.model.RecognizeResult

/**
 * Created by yongshan on 3/15/17.
 */
class RecognizeReposity {
    companion object {
        val CODE_OK = 0
        val CODE_SIZE_LIMIT = 429
        val CODE_ERROR = 430
    }
    val service = RestApiAdapter.createService(RecognizeApi::class.java)
    fun recognize(path: String, callBack: RecognizeCallBack) {
//        val image = File(path)
//        if (image.exists() && image.isFile) {
//            val body = RequestBody.create(MediaType.parse("image"), path)
//            service.recognize(body)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe({ t -> callBack.onSucccess(t)},
//                            {callBack.onFailure()})
//        } else {
//            callBack.fileLoadError()
//        }
    }

    interface RecognizeCallBack {
        fun onSucccess(result: RecognizeResult?)
        fun onFailure()
        fun fileLoadError()
        fun fileSizeLimitError()
    }
}