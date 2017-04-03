package com.hust.emotion.model

/**
 * Created by yongshan on 3/23/17.
 */
data class Face (
        var faceRectangle: FaceRectangle,
        var faceAttributes: FaceAttributes,
        var faceId: String
)
