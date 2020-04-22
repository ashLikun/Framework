package com.ashlikun.core.simple.mvvm

import com.ashlikun.core.mvvm.BaseViewModel
import com.ashlikun.utils.other.LogUtils

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/19　17:08
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class MainViewModel2 : BaseViewModel() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.e("onCreate")
    }

    override fun onStart() {
        super.onStart()
        LogUtils.e("onStart")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.e("onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.e("onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.e("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.e("onDestroy")
    }

    override fun onCleared() {
        super.onCleared()
        LogUtils.e("onCleared")
    }
}