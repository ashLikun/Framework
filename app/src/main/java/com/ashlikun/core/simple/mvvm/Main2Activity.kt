package com.ashlikun.core.simple.mvvm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.ashlikun.core.mvvm.BaseMvvmActivity
import com.ashlikun.core.mvvm.IViewModel
import com.ashlikun.utils.other.LogUtils
import com.ashlikun.core.simple.R

@IViewModel(MainViewModel::class)
class Main2Activity : BaseMvvmActivity<MainViewModel>() {
    override fun initViewModel() {
        super.initViewModel()
        LogUtils.e("initViewModel")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.e("onCreate")
        removeAll(supportFragmentManager)
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        LogUtils.e("getLayoutId")
        return R.layout.activity_main
    }


    override fun initView() {
        LogUtils.e("initView")
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment, Main2Fragment()).commit()
    }

    override fun parseIntent(intent: Intent?) {
        LogUtils.e("parseIntent")
    }

    override fun clearData() {
        LogUtils.e("clearData")
    }

    fun removeAll(fm: FragmentManager) {
        val ff = fm.fragments
        if (ff != null && !ff.isEmpty()) {
            val ft = fm.beginTransaction()
            for (f in ff) {
                ft.remove(f!!)
            }
            ft.commitNowAllowingStateLoss()
        }
    }
}
