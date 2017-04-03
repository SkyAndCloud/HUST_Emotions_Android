package com.hust.emotion.util

import android.app.Activity
import android.widget.ImageView
import com.lzy.imagepicker.loader.ImageLoader
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by yongshan on 3/15/17.
 */
class PicassoImageLoader : ImageLoader {

    override fun displayImage(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
        Picasso.with(activity)//
                .load(File(path))//
                .placeholder(com.hust.emotion.R.drawable.iv_placeholder)//
                .error(com.hust.emotion.R.drawable.iv_placeholder)//
                .resize(width, height)//
                .centerInside()//
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//
                .into(imageView)
    }

    override fun clearMemoryCache() {
        //这里是清除缓存的方法,根据需要自己实现
    }
}
