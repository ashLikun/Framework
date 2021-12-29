package com.ashlikun.core

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ashlikun.utils.other.ClassUtils
import com.ashlikun.utils.ui.extend.hineIme
import com.ashlikun.utils.ui.extend.showIme
import com.ashlikun.utils.ui.status.StatusBarCompat

/**
 * 作者　　: 李坤
 * 创建时间: 2021.12.29　11:06
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Activity和 Fragment相关扩展方法
 */

fun Activity.setStatusBarVisible(show: Boolean, statusBar: StatusBarCompat? = null) {
    if (show) {
        window.showIme()
    } else {
        window.hineIme()
    }
    statusBar?.setStatusDarkColor()
}

/**
 * 当前窗口亮度
 * 范围为0~1.0,1.0时为最亮，-1为系统默认设置
 */
var Activity.windowBrightness
    get() = window.attributes.screenBrightness
    set(brightness) {
        //小于0或大于1.0默认为系统亮度
        window.attributes = window.attributes.apply {
            screenBrightness = if (brightness >= 1.0 || brightness < 0) -1.0F else brightness
        }
    }


/**
 * 防止activity退出的时候动画重叠
 */
fun Activity.finishNoAnim() {
    overridePendingTransition(0, 0)
    finish()
}

/**
 * 新的启动api
 */
fun <I, O> ComponentActivity.registerForActivityResultX(
    contract: ActivityResultContract<I, O>,
    callback: (O) -> Unit
): ActivityResultLauncher<I> {
    var oldStatus: Lifecycle.State? = null
    //反射修改字段
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        oldStatus = lifecycle.currentState
        ClassUtils.setFieldValue(lifecycle, "mState", Lifecycle.State.CREATED)
    }
    //这段注册代码源码里面做了限制，必须在onStart之前，所以反射修改字段，骗过注册
    val launcher = registerForActivityResult(contract) {
        callback.invoke(it)
    }
    if (oldStatus != null) {
        ClassUtils.setFieldValue(lifecycle, "mState", oldStatus)
    }
    return launcher
}


/**
 * 启动activity,用新api registerForActivityResult
 */
fun ComponentActivity.launchForActivityResult(
    intent: Intent,
    checkCode: Boolean = true,
    success: ((ActivityResult) -> Unit)
): ActivityResultLauncher<Intent> {
    val launcher = registerForActivityResultX(ActivityResultContracts.StartActivityForResult()) {
        if (!checkCode || it.resultCode == Activity.RESULT_OK) {
            success.invoke(it)
        }
    }
    launcher.launch(intent)
    return launcher
}


/**
 * 新的启动api
 */
fun <I, O> Fragment.registerForActivityResultX(
    contract: ActivityResultContract<I, O>,
    callback: (O) -> Unit
): ActivityResultLauncher<I> {
    var oldStatus: Lifecycle.State? = null
    //反射修改字段
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        oldStatus = lifecycle.currentState
        ClassUtils.setFieldValue(lifecycle, "mState", Lifecycle.State.CREATED)
    }
    //这段注册代码源码里面做了限制，必须在onStart之前，所以反射修改字段，骗过注册
    val launcher = registerForActivityResult(contract) {
        callback.invoke(it)
    }
    if (oldStatus != null) {
        ClassUtils.setFieldValue(lifecycle, "mState", oldStatus)
    }
    return launcher
}

/**
 * 启动activity,用新api registerForActivityResult
 */
fun Fragment.launchForActivityResult(
    intent: Intent,
    checkCode: Boolean = true,
    success: ((ActivityResult) -> Unit)
): ActivityResultLauncher<Intent> {
    val launcher = registerForActivityResultX(ActivityResultContracts.StartActivityForResult()) {
        if (!checkCode || it.resultCode == Activity.RESULT_OK) {
            success.invoke(it)
        }
    }
    launcher.launch(intent)
    return launcher
}

