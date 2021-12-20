package com.ashlikun.core.simple.mvp

import android.util.Log
import com.ashlikun.core.fragment.BaseMvpFragment
import com.ashlikun.core.simple.R

class MainFragment : BaseMvpFragment<MainFragmentPresenter>(), IMainView {

    override val layoutId = R.layout.activity_main

    override fun initView() {
        Log.e("MainFragment", "initView")
    }


    override fun clearData() {

    }


    override fun findSize() = 111

}
