package com.hust.emotion.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo




/**
 * Created by yongshan on 3/24/17.
 */
class EmotionUtils {
    companion object {
        /**
         * 获取活动网络信息

         * @param context 上下文
         * *
         * @return NetworkInfo
         */
        fun getActiveNetworkInfo(context: Context): NetworkInfo? {
            return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        }

        /**
         * 判断网络是否可用
         *
         * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`

         * @param context 上下文
         * *
         * @return `true`: 可用<br></br>`false`: 不可用
         */
        fun isAvailable(context: Context): Boolean {
            val info = getActiveNetworkInfo(context)
            return info != null && info.isAvailable
        }

    }
}