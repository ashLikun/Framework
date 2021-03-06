package com.ashlikun.core.simple

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import com.ashlikun.core.simple.R
import com.ashlikun.core.activity.BaseMvpActivity
import com.ashlikun.core.simple.databinding.ActivityMainBinding
import com.ashlikun.core.simple.databinding.ActivityMainMvpBinding

class MainActivity : BaseMvpActivity<MainPresenter>(), IMainView {

    val viewBinding by lazy {
        ActivityMainMvpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.e("MainActivity", "onCreate")
    }


    override fun initView() {
        Log.e("MainActivity", "initView")
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment, MainFragment()).commit()
    }

    override fun parseIntent(intent: Intent) {
        Log.e("MainActivity", "parseIntent" + (presenter == null))
    }

    override fun clearData() {

    }

    override fun findSize() = 111
}
