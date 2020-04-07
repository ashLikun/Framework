package com.ashlikun.core.mvvm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.ashlikun.core.listener.OnDispatcherMessage
import com.ashlikun.loadswitch.ContextData
import com.ashlikun.loadswitch.LoadSwitchService
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.utils.ui.ActivityManager

/**
 * 作者　　: 李坤
 * 创建时间: 2020/3/27　16:39
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：VM 的基础,自动感知生命周期
 */
open abstract class BaseViewModel : ViewModel(), LifecycleObserver, OnDispatcherMessage {
    //VM是否Cleared
    var isCleared = false
        private set

    //LiveData创建
    val lifeDataProvider: LiveDataProvider by lazy {
        LiveDataProvider()
    }

    //上下文
    var context: Context? = null

    //布局切换
    var loadSwitchService: LoadSwitchService? = null

    //数据是否初始化过
    var dataInit = false

    //感知页面生命周期
    var lifecycleOwner: LifecycleOwner? = null

    //清空数据
    internal val clearData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //添加观察者,实现感知页面生命周期
    val addObserver: MutableLiveData<LifecycleObserver> by lazy {
        MutableLiveData<LifecycleObserver>()
    }

    //销毁页面
    internal val finish: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //显示加载布局
    internal val showLoading: MutableLiveData<ContextData> by lazy {
        MutableLiveData<ContextData>()
    }

    //显示重新加载页面
    internal val showRetry: MutableLiveData<ContextData> by lazy {
        MutableLiveData<ContextData>()
    }

    //显示内容
    internal val showContent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //显示空页面
    internal val showEmpty: MutableLiveData<ContextData> by lazy {
        MutableLiveData<ContextData>()
    }

    open fun clearData() {
        clearData.postValue("")
    }

    open fun finish() {
        finish.postValue("")
    }

    open fun showLoading(contextData: ContextData) {
        showLoading.postValue(contextData)
    }

    open fun showContent(str: String = "") {
        showContent.postValue(str)
    }

    open fun showEmpty(contextData: ContextData) {
        showEmpty.postValue(contextData)
    }

    /**
     * 创建的时候,一般用于主动获取数据
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
    }

    /**
     * 解析意图数据
     */
    open fun parseIntent(intent: Intent) {

    }

    /**
     * 获取前台Activity
     */
    open fun getActivity(): Activity? = ActivityManager.getForegroundActivity()


    /**
     * UI发送过来的事件
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    override fun onDispatcherMessage(what: Int, bundle: Bundle?) {}

    override fun onCleared() {
        super.onCleared()
        isCleared = true
        lifecycleOwner = null
        context = null
        loadSwitchService = null
        cancelAllHttp()
    }

    /**
     * 销毁网络访问
     */
    open fun cancelAllHttp() {
        OkHttpUtils.getInstance().cancelTag(this)
    }

    /**
     * 通过指定的数据实体类获取对应的MutableLiveData类
     */
    open operator fun <T> get(clazz: Class<T>) = lifeDataProvider[clazz]

    /**
     * 通过指定的key或者数据实体类获取对应的MutableLiveData类
     */
    open operator fun <T> get(key: String?, clazz: Class<T>) = lifeDataProvider[key, clazz]
}