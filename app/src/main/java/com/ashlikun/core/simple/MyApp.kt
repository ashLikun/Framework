package com.ashlikun.core.simple

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.ashlikun.core.BaseUtils
import com.ashlikun.okhttputils.http.OkHttpManage
import com.ashlikun.orm.LiteOrmUtil
import com.ashlikun.utils.AppUtils
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * 作者　　: 李坤
 * 创建时间: 2018/1/22　13:52
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class MyApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppUtils.isDebug = true
        AppUtils.init(this, "Base")
        ARouter.init(this)
        LiteOrmUtil.init(this)
        OkHttpManage.init(this, null)
        BaseUtils.coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
            Log.e("MyApp", "coroutineExceptionHandler")
            t.printStackTrace()
        }
    }
}