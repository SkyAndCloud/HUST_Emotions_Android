package com.hust.emotion

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import me.shaohui.shareutil.ShareConfig
import me.shaohui.shareutil.ShareManager



/**
 * Created by yongshan on 3/23/17.
 */
class EmotionApp: Application() {
    companion object {
        private val WX_APP_ID = "wxc5b1d819b59f1283"
    }
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this);
        // init
        val config = ShareConfig.instance().wxId(WX_APP_ID)
        ShareManager.init(config)
    }
}