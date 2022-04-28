package com.ashlikun.core.simple.mvvm

import com.ashlikun.core.mvvm.BaseViewModel
import com.ashlikun.core.simple.Data
import com.ashlikun.utils.other.LogUtils

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/19　17:08
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class MainViewModel : BaseViewModel() {
    val mainData = get<Data?>()
    val mainData2 = get<List<String>>()
    override fun onCreate() {
//        mainData.value = mutableListOf("dddddddddd")
        super.onCreate()
        LogUtils.e("ViewModel onCreate ${System.identityHashCode(this)}")
    }

    override fun onStart() {
        super.onStart()
        LogUtils.e("ViewModel onStart")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.e("ViewModel onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.e("ViewModel onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.e("ViewModel onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.e("ViewModel onDestroy")
    }

    override fun onCleared() {
        super.onCleared()
        LogUtils.e("ViewModel onCleared")
    }
}

