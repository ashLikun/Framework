package com.ashlikun.core.mvvm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import com.ashlikun.core.listener.OnDispatcherMessage
import com.ashlikun.loadswitch.ContextData
import com.ashlikun.loadswitch.LoadSwitchService
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.utils.main.ActivityUtils
import com.ashlikun.utils.ui.ActivityManager

/**
 * 作者　　: 李坤
 * 创建时间: 2020/3/27　16:39
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：VM 的基础,自动感知生命周期
 */

open abstract class BaseViewModel : ViewModel(), LifecycleObserver, OnDispatcherMessage {
    //这几个是和对宿主的引用，这里得额外处理,防止内存泄漏和null指针异常
    //标记是否与宿主断开，就是宿主是否销毁（准备开始重建）
    internal var isDestroy = false

    //上下文
    var context: Context? = null

    //布局切换
    var loadSwitchService: LoadSwitchService? = null

    //感知页面生命周期
    var lifecycleOwner: LifecycleOwner? = null

    //这几个是和对宿主的引用，这里得额外处理,防止内存泄漏和null指针异常 结束


    //VM是否Cleared
    var isCleared = false
        private set

    //LiveData创建
    val lifeDataProvider: LiveDataProvider by lazy {
        LiveDataProvider()
    }

    //数据是否初始化过
    var dataInit = false

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
        clearData.value = ""
    }

    open fun finish() {
        finish.value = ""
    }

    open fun showLoading(contextData: ContextData) {
        showLoading.value = contextData
    }

    open fun showContent(str: String = "") {
        showContent.value = str
    }

    open fun showEmpty(contextData: ContextData) {
        showEmpty.value = contextData
    }

    open fun showRetry(contextData: ContextData) {
        showRetry.value = contextData
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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        //防止内存泄漏
        lifecycleOwner = null
        context = null
        loadSwitchService = null
        isDestroy = true
    }

    /**
     * 解析意图数据
     */
    open fun parseIntent(intent: Intent) {

    }

    /**
     * onNewIntent 触发的调用
     */
    open fun newData() {
        
    }

    /**
     * 获取前台Activity
     */
    open fun getActivity(): Activity? = ActivityUtils.getActivity(context)
            ?: ActivityManager.getForegroundActivity()

    /**
     * UI发送过来的事件
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    override fun onDispatcherMessage(what: Int, bundle: Bundle?) {}

    /**
     * 处理fragment发送过来的数据
     *
     * @param what:事件类型
     * @param data      事件传递的数据
     */
    override fun onDispatcherMessage(what: Int, data: Any?) {}

    /**
     * 提供数据给Fragment
     *
     * @param what:事件类型
     * @return 事件传递的数据
     */
    override fun <T : Any?> getDispatcherMessage(what: Int): T? = null
    override fun onCleared() {
        super.onCleared()
        cancelAllHttp()
        isCleared = true
        lifecycleOwner = null
        context = null
        loadSwitchService = null
        isDestroy = false
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