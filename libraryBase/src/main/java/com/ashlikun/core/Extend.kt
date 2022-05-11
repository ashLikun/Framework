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
import java.util.concurrent.atomic.AtomicInteger

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

val atomicInteger = AtomicInteger()

/**
 * 新的启动api
 * @param contract [ActivityResultContract.StartActivityForResult]  or  [ActivityResultContracts.RequestMultiplePermissions()]
 * @param isUnregister 是否返回的时候 释放
 * @param key key的前缀
 */
fun <I, O> ComponentActivity.registerForActivityResultX(
    contract: ActivityResultContract<I, O>,
    isUnregister: Boolean = true,
    key: String = "ForActivityResult",
    callback: (O) -> Unit
): ActivityResultLauncher<I> {
    var launcher: ActivityResultLauncher<I>? = null
    //这种注册需要自己unregister
    launcher = activityResultRegistry.register(key + atomicInteger.getAndIncrement(), contract) {
        callback.invoke(it)
        //这里主动释放
        if (isUnregister)
            launcher?.unregister()
    }
    return launcher
}


/**
 * 启动activity,用新api registerForActivityResult
 * @param checkCode 是否只有Activity.RESULT_OK 才回调成功
 */
fun ComponentActivity.launchForActivityResult(
    intent: Intent, checkCode: Boolean = false,
    isUnregister: Boolean = true,
    key: String = "launchForActivityResult",
    success: ((ActivityResult) -> Unit)
): ActivityResultLauncher<Intent> {
    return registerForActivityResultX(ActivityResultContracts.StartActivityForResult(), key = key, isUnregister = isUnregister) {
        if (!checkCode || it.resultCode == Activity.RESULT_OK) {
            success.invoke(it)
        }
    }.apply { launch(intent) }
}


/**
 * 新的启动api
 */
fun <I, O> Fragment.registerForActivityResultX(
    contract: ActivityResultContract<I, O>, isUnregister: Boolean = true,
    key: String = "ForActivityResult",
    callback: (O) -> Unit
) = requireActivity().registerForActivityResultX(contract, isUnregister, key, callback)

/**
 * 启动activity,用新api registerForActivityResult
 * @param checkCode 是否只有Activity.RESULT_OK 才回调成功
 */
fun Fragment.launchForActivityResult(
    intent: Intent, checkCode: Boolean = false, isUnregister: Boolean = true,
    key: String = "launchForActivityResult",
    success: ((ActivityResult) -> Unit)
): ActivityResultLauncher<Intent> = requireActivity().launchForActivityResult(intent, checkCode, isUnregister, key, success)

