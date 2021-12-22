//package com.ashlikun.core.simple.mvp
//
//import android.os.Bundle
//import com.ashlikun.core.BasePresenter
//import com.ashlikun.utils.other.LogUtils.e
//
///**
// * 作者　　: 李坤
// * 创建时间: 2017/12/19　17:08
// * 邮箱　　：496546144@qq.com
// *
// *
// * 功能介绍：
// */
//class MainFragmentPresenter : BasePresenter<IMainView>() {
//    override fun onCreate(savedInstanceState: Bundle) {
//        super.onCreate(savedInstanceState)
//        e("onCreate")
//    }
//
//    override fun onAttachView(mvpView: IMainView) {
//        super.onAttachView(mvpView)
//        e("onAttachView")
//    }
//
//    override fun onStart() {
//        super.onStart()
//        e("onStart")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        e("onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        e("onPause")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        e("onStop")
//    }
//}