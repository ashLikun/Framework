package com.ashlikun.core.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.ashlikun.core.BaseUtils
import com.ashlikun.core.R
import com.ashlikun.core.listener.IBaseWindow
import com.ashlikun.core.listener.OnDispatcherMessage
import com.ashlikun.loadswitch.ContextData
import com.ashlikun.loadswitch.LoadSwitch
import com.ashlikun.loadswitch.LoadSwitchService
import com.ashlikun.okhttputils.http.OkHttpManage
import com.ashlikun.supertoobar.SuperToolBar
import com.ashlikun.utils.bug.BugUtils
import com.ashlikun.utils.ui.status.StatusBarCompat
import kotlin.math.abs

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.20 15:56
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：自定义最顶层的Activity
 * @see IBaseWindow : 只要是窗口都会实现这个统一接口
 *
 * @see OnDispatcherMessage : Activity处理其他
 */

abstract class BaseActivity : AppCompatActivity(), IBaseWindow, OnDispatcherMessage {

    private val activityResultCalls: MutableMap<Int, (ActivityResult) -> Unit> by lazy {
        mutableMapOf()
    }
    open val thisMy by lazy {
        this
    }

    /**
     * 当调用Activity的getResources将被调用，便于hook,只调用一次，内部会缓存
     */
    protected open val myResources: Resources by lazy {
        var result = super.getResources()
        BaseUtils.activityGetResources?.forEach {
            result = it(result)
        }
        result
    }

    //与Fragment方法统一 Context
    override val requireContext: Context
        get() = this

    //与Fragment方法统一  activity
    open val requireActivity: BaseActivity
        get() = this

    //请求CODE
    open val REQUEST_CODE by lazy {
        abs(this.javaClass.simpleName.hashCode() % 60000)
    }

    //toolbar
    open val toolbar: SuperToolBar? by lazy {
        f(R.id.toolbar)
    }

    //状态栏
    open val statusBar: StatusBarCompat? by lazy {
        StatusBarCompat(this)
    }

    //布局切换的根布局
    override val switchRoot: View? by lazy {
        f(R.id.switchRoot)
    }

    //布局切换
    override val switchService: LoadSwitchService? by lazy {
        if (switchRoot == null) null else LoadSwitch()
            .create(switchRoot!!, createSwitchLayoutListener())
    }

    override fun getResources(): Resources {
        return myResources
    }

    var baseContextIsChang = false
    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        var newConfiguration = overrideConfiguration
        BaseUtils.applyOverrideConfiguration?.forEach {
            newConfiguration = it(newConfiguration)
        }
        super.applyOverrideConfiguration(newConfiguration)
    }

    override fun attachBaseContext(newBase: Context) {
        var newContext = newBase
        BaseUtils.attachBaseContext?.forEach {
            newContext = it(newContext)
            baseContextIsChang = newContext != newBase
        }
        super.attachBaseContext(newContext)

    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        var newConfiguration = newConfig
        BaseUtils.configurationChanged?.forEach {
            newConfiguration = it(newConfiguration)
        }
        super.onConfigurationChanged(newConfiguration)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        BaseUtils.activityPreCreated?.forEach {
            it(this, savedInstanceState)
        }
        BugUtils.orientationBug8_0(this)
        super.onCreate(savedInstanceState)
        if (intent == null) {
            intent = Intent()
        }
        parseIntent(intent)
        setActivityContentView()
        setStatueBar()
        baseInitView()
        initView()
        initData()
    }

    override fun onNewIntent(intent: Intent) {
        var intent: Intent? = intent
        super.onNewIntent(intent)
        if (intent == null) {
            intent = Intent()
        }
        setIntent(intent)
        parseIntent(intent)
        newData()
    }

    /**
     * 设置状态栏
     */
    protected open fun setStatueBar() {
        if (isStatusBarEnable) {
            //设置状态栏颜色兼容,默认只是颜色
            if (isStatusTranslucent || isStatusTranslucentAndroidMHalf) {
                statusBar?.translucentStatusBar(isStatusTranslucentAndroidMHalf)
            } else {
                statusBar?.setStatusBarColor(statusBarColor)
            }
        }
    }

    /**
     * 设置activity的布局，可以重写
     */
    protected open fun setActivityContentView() {
        val layoutId = layoutId
        if (layoutId != View.NO_ID) {
            setContentView(layoutId)
        } else {
            var view: View? = contentView
            if (view != null) {
                setContentView(view)
            } else {
                view = binding?.root
                view?.let { setContentView(it) }
            }
        }
    }

    /**
     * 基本的View初始化
     */
    protected open fun baseInitView() {
        initLoadSwitch()
    }

    override fun <T : View?> f(@IdRes id: Int): T? {
        return findViewById(id)
    }

    /**
     * 初始化布局切换的管理器
     */
    override fun initLoadSwitch() {
        val view = switchRoot
        if (view != null) {
            //这里得初始化
            switchService
        }
    }

    /**
     * onNewIntent 触发的调用
     */
    protected open fun newData() {}

    /**
     * ：解析意图
     */
    protected open fun parseIntent(intent: Intent) {
        runCatching {
            ARouter.getInstance().inject(this)
        }
    }

    /**
     * 获取状态栏颜色
     */
    open val statusBarColor: Int
        get() {
            //获取主题颜色
            val array = theme.obtainStyledAttributes(intArrayOf(R.attr.statusColorCustom))
            val color = array.getColor(0, -0x1)
            array.recycle()
            return color
        }

    /**
     * 内容是不是放到状态栏里面
     */
    open val isStatusTranslucent: Boolean
        get() = false

    /**
     * 6.0以下是否绘制半透明,因为不能设置状态栏字体颜色
     */
    open val isStatusTranslucentAndroidMHalf: Boolean
        get() = false

    /**
     * 状态栏是否开启沉浸式
     */
    open val isStatusBarEnable: Boolean
        get() = true


    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllHttp()
        activityResultCalls.clear()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        cancelAllHttp()
        activityResultCalls.clear()
    }


    override fun showLoading(data: ContextData, isForce: Boolean) {
        if (isForce) {
            switchService?.isLoadingCanShow = true
        }
        switchService?.showLoading(data)
    }


    override fun showContent() {
        switchService?.showContent()
    }

    override fun showEmpty(data: ContextData) {
        switchService?.showEmpty(data)
    }

    override fun showRetry(data: ContextData) {
        switchService?.showRetry(data)
    }

    override fun finish() {
        super.finish()
    }

    /**
     * 销毁网络访问
     */
    override fun cancelAllHttp() {
        OkHttpManage.cancelTag(this)
    }

    /**
     * 处理fragment发送过来的事件
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    override fun onDispatcherMessage(what: Int, bundle: Bundle) {}

    /**
     * 处理fragment发送过来的数据
     *
     * @param what:事件类型
     * @param data      事件传递的数据
     */
    override fun onDispatcherMessage(what: Int, data: Any) {}

    /**
     * 提供数据给Fragment
     *
     * @param what:事件类型
     * @return 事件传递的数据
     */
    override fun <T> getDispatcherMessage(what: Int): T? {
        return null
    }

    open fun getUserVisibleHint(): Boolean {
        return true
    }

    fun registerResultCall(requestCode: Int, call: (ActivityResult) -> Unit) {
        activityResultCalls[requestCode] = call
    }

    fun unRegisterResultCall(requestCode: Int) {
        activityResultCalls.remove(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultCalls[requestCode]?.invoke(ActivityResult(requestCode, data))
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        BaseUtils.userInteraction?.forEach {
            it(this)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        BaseUtils.dispatchTouchEvent?.forEach {
            if (it(this, ev)) return true
        }
        return super.dispatchTouchEvent(ev)
    }
}