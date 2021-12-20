package com.ashlikun.core.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    /**
     * 请求CODE
     */
    var REQUEST_CODE = Math.abs(this.javaClass.simpleName.hashCode() % 60000)

    /**
     * 宿主activity
     */
    val requireActivity: FragmentActivity
        get() = requireActivity()

    /**
     * 宿主Context
     */
    val requireContext: Context
        get() = requireContext()

    /**
     * 布局
     */
    protected var rootView: View? = null

    /**
     * 布局切换
     */
    override var switchService: LoadSwitchService? = null

    /**
     * 是否是回收利用的Fragment
     */
    @JvmField
    protected var isRecycle = false
    protected var toolbar: SuperToolBar? = null
    override var switchRoot: View? = null

    val activitySupper: BaseActivity?
        get() = if (activity is BaseActivity) activity as BaseActivity? else null
    val activityStatusBar: StatusBarCompat?
        get() = activitySupper?.statusBar

    /**
     * ViewBinding的Class
     */
    protected var viewBindingClass: Class<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        if (arguments != null) {
            intent.putExtras(arguments!!)
        }
        parseIntent(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            isRecycle = false
            setContentView()
        } else {
            isRecycle = true
            val parent: ViewGroup = rootView!!.getParent() as ViewGroup
            if (parent != null) {
                parent.removeView(rootView)
            }
        }
        return rootView
    }

    /**
     * 设置布局，可以重写
     */
    protected fun setContentView() {
        val layoutId: Int = layoutId
        if (layoutId != View.NO_ID) {
            rootView = UiUtils.getInflaterView(requireActivity, layoutId)
        } else {
            rootView = contentView
            if (rootView == null) {
                //通过反射获取ViewBinding
                if (viewBindingClass != null) {
                    rootView = BaseUtils.getViewToViewBindingClass(viewBindingClass, layoutInflater)
                }
                if (rootView == null) {
                    rootView = BaseUtils.getViewBindingView(this)
                }
            }
        }
    }

    override fun initLoadSwitch() {
        if (switchRoot != null) {
            switchService = LoadSwitch.get()
                .register(switchRoot!!, BaseUtils.getSwitchLayoutListener(requireContext, this))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    protected open fun baseInitView() {
        toolbar = f(R.id.toolbar)
        switchRoot = f(R.id.switchRoot)
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
    fun onBackPressed(): Boolean {
        return false
    }

    override fun onLowMemory() {
        super.onLowMemory()
        cancelAllHttp()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllHttp()
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
    fun sendMsgToActivity(what: Int, bundle: Bundle = Bundle()) {
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
}