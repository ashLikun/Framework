package com.ashlikun.core.simple

import android.util.Log

import com.ashlikun.core.fragment.BaseMvpFragment

class MainFragment : BaseMvpFragment<MainFragmentPresenter>(), IMainView {

    override val layoutId = R.layout.activity_main


    override fun initView() {
        Log.e("MainFragment", "initView")
    }


    override fun clearData() {

    }

    override fun findSize() = 111
}
