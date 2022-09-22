package com.ashlikun.core.simple.mvvm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.FragmentManager
import com.ashlikun.core.launchForActivityResult
import com.ashlikun.core.mvvm.BaseMvvmActivity
import com.ashlikun.core.mvvm.IViewModel
import com.ashlikun.core.mvvm.launch
import com.ashlikun.core.registerForActivityResultX
import com.ashlikun.core.simple.R
import com.ashlikun.core.simple.databinding.ActivityMainBinding
import com.ashlikun.utils.other.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler

@IViewModel(MainViewModel::class)
class Main2Activity : BaseMvvmActivity<MainViewModel>() {
    override val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

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
            Log.e("Main2Activity", "onclick1: 11111")
            t.printStackTrace()
        }) {
            throw NullPointerException()
        }
        launchForActivityResult(Intent(this, TestActivity::class.java)) {
            LogUtils.e("dddd" + it.data?.getStringExtra("aaa"))
            launchForActivityResult(Intent(this, TestActivity::class.java)) {
                LogUtils.e("dddd" + it.data?.getStringExtra("aaa"))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


    override fun initView() {
        LogUtils.e("initView")
        if (supportFragmentManager.fragments.size == 0) {
            val f1 = Main2Fragment()
            val f2 = Main2Fragment()
            LogUtils.e("initView")
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, f1)
//                .add(R.id.fragment, f2)

                .commit()
        }
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
