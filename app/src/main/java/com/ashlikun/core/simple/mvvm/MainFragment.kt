package com.ashlikun.core.simple

import com.ashlikun.core.mvvm.BaseMvvmFragment
import com.ashlikun.utils.other.LogUtils

class Main2Fragment : BaseMvvmFragment<MainViewModel2>() {


    override fun getLayoutId(): Int {
        LogUtils.e("getLayoutId")
        return R.layout.activity_main

    }

    override fun initView() {
        LogUtils.e("initView")
    }
}
