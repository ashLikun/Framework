package com.ashlikun.core.simple.mvvm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import com.ashlikun.core.mvvm.BaseMvvmActivity
import com.ashlikun.core.mvvm.IViewModel
import com.ashlikun.core.mvvm.launch
import com.ashlikun.core.simple.databinding.ActivityMainBinding
import com.ashlikun.utils.other.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler

@IViewModel(MainViewModel::class)
class Main2Activity : BaseMvvmActivity<MainViewModel>() {
    val viewBinding by lazy {
        val aaa = System.currentTimeMillis()
        val aa = ActivityMainBinding.inflate(layoutInflater)
        Log.e("addssssssssss", (System.currentTimeMillis() - aaa).toString() )
        aa
    }

    override fun initViewModel() {
        super.initViewModel()
        LogUtils.e("initViewModel")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.e("onCreate ${System.identityHashCode(this)}")
        super.onCreate(savedInstanceState)
    }

    fun onclick1(v: View) {
        launch(context = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        }) { }
    }

    override fun initView() {
//        if (supportFragmentManager.fragments.size == 0) {
//            val f1 = Main2Fragment()
//            val f2 = Main2Fragment()
//            LogUtils.e("initView")
//            supportFragmentManager.beginTransaction()
//                    .add(R.id.fragment, f1)
////                .add(R.id.fragment, f2)
//
//                    .commit()
//        }
    }

    override fun parseIntent(intent: Intent) {
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
