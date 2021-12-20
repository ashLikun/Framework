package com.ashlikun.core.listener

import android.os.Bundle

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/19 19:54
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Activity处理接口事件（如fragment发生一条事件给Activity处理）
 */

interface OnDispatcherMessage {
    /**
     * 处理回掉事件
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    fun onDispatcherMessage(what: Int, bundle: Bundle)
    fun onDispatcherMessage(what: Int, obj: Any)
    fun <T> getDispatcherMessage(what: Int): T?
}