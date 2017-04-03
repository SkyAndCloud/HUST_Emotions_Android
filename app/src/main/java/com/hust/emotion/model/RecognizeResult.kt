package com.hust.emotion.model

/**
 * Created by yongshan on 3/23/17.
 */
data class RecognizeResult (
    var code: Int,
    var msg: String,
    var face: Face,
    var time: Float
)