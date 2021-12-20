package com.ashlikun.core.iview

import android.content.Context
import com.ashlikun.loadswitch.ContextData

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.20 10:33
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：抽离Mvp的View
 */

interface IBaseView {

    /**
     * 清空数据  一般在列表使用  但是也可以作为其他的界面使用
     */
    fun clearData()

    fun getContext(): Context?

    /**
     * 显示加载中布局
     *
     * @param data
     */
    fun showLoading(data: ContextData)

    /**
     * 显示重新加载页面
     *
     * @param data
     */
    fun showRetry(data: ContextData)

    /**
     * 显示内容页面
     */
    fun showContent()

    /**
     * 显示空页面
     *
     * @param data
     */
    fun showEmpty(data: ContextData)

    /**
     * Fragment是否选中，只有Fragment才有
     * 一般是ViewPager嵌套Fragment
     *
     * @return true:选中，false：未选中
     */
    fun getUserVisibleHint(): Boolean

    fun finish()
}