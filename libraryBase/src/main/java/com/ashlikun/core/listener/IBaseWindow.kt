package com.ashlikun.core.listener

import android.view.View
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding
import com.ashlikun.loadswitch.ContextData
import com.ashlikun.loadswitch.LoadSwitchService
import com.ashlikun.loadswitch.OnLoadSwitchClick

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/19 19:54
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：一个窗口要实现的接口
 */
interface IBaseWindow : OnLoadSwitchClick {
    /**
     * 获取布局id
     * 优先使用
     */
    val layoutId: Int
        get() = View.NO_ID

    /**
     * 获取布局id
     * 优先使用
     */
    val binding: ViewBinding?
        get() = null

    /**
     * 获取布局view
     * 次优先使用
     * @return
     */
    val contentView: View?
        get() = null

    /**
     * 获取需要转化为[LoadSwitchService]的控件
     *
     * @return
     */
    val switchRoot: View?

    /**
     * 获取自动切换加载中布局的管理器
     */
    val switchService: LoadSwitchService?
        get() = null

    /**
     * 显示不同的界面布局 监听器
     */
    val onLoadSwitchClick: OnLoadSwitchClick
        get() = this

    /**
     * 初始化view
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData() {}

    /**
     * 显示加载中布局
     *
     * @param data
     */
    fun showLoading(data: ContextData = ContextData())

    /**
     * 初始化布局切换的管理器
     */
    fun initLoadSwitch()

    /**
     * 显示重新加载页面
     *
     * @param data
     */
    fun showRetry(data: ContextData = ContextData())

    /**
     * 显示内容页面
     */
    fun showContent()

    /**
     * 显示空页面
     *
     * @param data
     */
    fun showEmpty(data: ContextData = ContextData())

    /**
     * 销毁页面
     */
    fun finish()

    /**
     * 销毁网络访问
     */
    fun cancelAllHttp()

    /**
     * 自定义查找控件
     *
     * @param id
     * @param <T>
     * @return
    </T> */
    fun <T : View?> f(@IdRes id: Int): T?
    override fun onRetryClick(data: ContextData) {}
    override fun onEmptyClick(data: ContextData) {}
}