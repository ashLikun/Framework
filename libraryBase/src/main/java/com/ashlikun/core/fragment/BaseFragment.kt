package com.ashlikun.core.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.ashlikun.core.BaseUtils
import com.ashlikun.core.R
import com.ashlikun.core.activity.BaseActivity
import com.ashlikun.core.listener.IBaseWindow
import com.ashlikun.core.listener.OnDispatcherMessage
import com.ashlikun.loadswitch.ContextData
import com.ashlikun.loadswitch.LoadSwitch
import com.ashlikun.loadswitch.LoadSwitchService
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.supertoobar.SuperToolBar
import com.ashlikun.utils.ui.UiUtils
import com.ashlikun.utils.ui.status.StatusBarCompat
import kotlin.math.abs

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/8 15:56
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：自定义最顶层的Fragment
 * @see IBaseWindow : 只要是窗口都会实现这个统一接口
 *
 * @see OnDispatcherMessage : Fragment处理其他
 */
abstract class BaseFragment : Fragment(), IBaseWindow, OnDispatcherMessage {
    //请求CODE
    open var REQUEST_CODE = abs(this.javaClass.simpleName.hashCode() % 60000)
    private val activityResultCalls: MutableMap<Int, (ActivityResult) -> Unit> by lazy {
        mutableMapOf()
    }

    //toolbar
    open val toolbar: SuperToolBar? by lazy {
        f(R.id.toolbar)
    }

    //根布局
    open protected var rootView: View? = null


    //布局切换的根布局
    override val switchRoot: View? by lazy {
        f(R.id.switchRoot)
    }

    //布局切换
    override val switchService: LoadSwitchService? by lazy {
        if (switchRoot == null) null else LoadSwitch()
            .create(switchRoot!!, createSwitchLayoutListener())
    }

    //是否是回收利用的Fragment
    open protected var isRecycle = false

    //是否懒加载 布局
    open protected val isLazy = false

    //懒加载是否完成
    private var isLazyOk = false

    //宿主activity
    open val requireActivity: FragmentActivity
        get() = requireActivity()

    //宿主Context
    override val requireContext: Context
        get() = requireContext()

    //处理arguments 为null的请情况
    open val requireArguments: Bundle
        get() {
            if (arguments == null) arguments = Bundle()
            return requireArguments()
        }

    //BaseActivity
    open val activitySupper: BaseActivity?
        get() = if (activity is BaseActivity) activity as BaseActivity else null

    //状态栏
    open val activityStatusBar: StatusBarCompat?
        get() = activitySupper?.statusBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        if (arguments != null) {
            intent.putExtras(requireArguments)
        }
        parseIntent(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            isRecycle = false
            if (!isLazy) {
                setContentView()
            } else {
                //懒加载模式,使用一个空布局
                rootView = FrameLayout(inflater.context)
            }

        } else {
            isRecycle = true
            (rootView?.parent as? ViewGroup)?.apply {
                removeView(rootView)
            }
        }
        return rootView
    }

    /**
     * 设置布局，可以重写
     */
    open protected fun setContentView() {
        val layoutId: Int = layoutId
        if (layoutId != View.NO_ID) {
            rootView = UiUtils.getInflaterView(requireActivity, layoutId)
        } else {
            rootView = contentView
            if (rootView == null) {
                rootView = binding?.root
            }
        }
    }

    override fun initLoadSwitch() {
        if (switchRoot != null) {
            //这里得初始化
            switchService
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isLazy) {
            onMyCreated()
        }
    }

    override fun onResume() {
        if (isLazy && !isLazyOk) {
            //记录默认布局
            val parent = rootView as? ViewGroup
            //加载真实布局
            setContentView()
            (rootView?.parent as? ViewGroup)?.apply {
                removeView(rootView)
            }
            //添加到中间布局
            parent?.addView(rootView)
            isLazyOk = true
            onMyCreated()
        }
        super.onResume()
    }

    private fun onMyCreated() {
        if (!isRecycle) {
            baseInitView()
            initView()
            initData()
        }

    }

    /**
     * 解析意图
     */
    protected open fun parseIntent(intent: Intent) {
        try {
            ARouter.getInstance().inject(this)
        } catch (e: Exception) {
        }
    }

    open protected fun baseInitView() {
        initLoadSwitch()
    }

    override fun <T : View?> f(@IdRes id: Int): T? {
        return rootView?.findViewById(id)
    }


    override fun onDetach() {
        super.onDetach()
    }

    /**
     * 当activity返回的时候,需要配合activity调用
     *
     * @return false:默认不处理  true:fragment处理
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onLowMemory() {
        super.onLowMemory()
        cancelAllHttp()
        activityResultCalls.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllHttp()
        activityResultCalls.clear()
    }

    override fun showLoading(data: ContextData) {
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
        if (activity?.isFinishing == false) {
            activity?.finish()
        }
    }

    /**
     * 销毁网络访问
     */
    override fun cancelAllHttp() {
        OkHttpUtils.get().cancelTag(this)
    }

    /**
     * 发送事件给activity
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    open fun sendMsgToActivity(what: Int, bundle: Bundle = Bundle()) {
        activitySupper?.onDispatcherMessage(what, bundle)
    }

    /**
     * 处理Activity发送过来的事件
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
}