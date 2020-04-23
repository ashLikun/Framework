package com.ashlikun.core.simple.mvvm

import android.content.Intent
import android.os.Bundle
import com.ashlikun.core.mvvm.BaseMvvmFragment
import com.ashlikun.core.simple.R
import com.ashlikun.utils.other.LogUtils

class Main2Fragment : BaseMvvmFragment<MainViewModel2>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.e("onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun initViewModel() {
        super.initViewModel()
        LogUtils.e("initViewModel")
    }

    override fun getLayoutId(): Int {
        LogUtils.e("getLayoutId")
        return R.layout.activity_main

    }

    override fun initView() {
        LogUtils.e("initView")
    }

    override fun parseIntent(intent: Intent) {
        LogUtils.e("parseIntent")
    }

    override fun clearData() {
        LogUtils.e("clearData")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.e("onResume")
    }
}
