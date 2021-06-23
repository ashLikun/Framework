package com.ashlikun.core.mvvm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.service.SerializationService
import com.alibaba.android.arouter.launcher.ARouter
import com.ashlikun.utils.ui.ActivityManager
import java.io.Serializable

/**
 * 作者　　: 李坤
 * 创建时间: 2020/6/8　17:54
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
typealias OnNvCallback = (postcard: Postcard) -> Unit
typealias ARouterNavigation = (postcard: Postcard) -> Any?

/**
 * 判断extra 是否有某个flag
 */
fun Postcard.hasFlag(flag: Int) = extra and flag == flag

/**
 * 移除extra的flag
 */
fun Postcard.removeFlag(flag: Int): Postcard {
    this.extra = extra and flag.inv()
    return this
}

/**
 * extra添加flag
 */
fun Postcard.addFlag(flag: Int): Postcard {
    this.extra = this.extra or flag
    return this
}

fun Postcard.navigation(
        context: Context? = ActivityManager.getInstance().currentActivity(),
        onArrival: OnNvCallback? = null,
        onLost: OnNvCallback? = null,
        onInterrupt: OnNvCallback? = null,
        onFound: OnNvCallback? = null,
) {
    navigation(context, if (onFound == null) null else object : NavigationCallback {
        override fun onFound(postcard: Postcard) {
            onFound?.invoke(postcard)
        }

        override fun onLost(postcard: Postcard) {
            onLost?.invoke(postcard)
        }

        override fun onArrival(postcard: Postcard) {
            onArrival?.invoke(postcard)
        }

        override fun onInterrupt(postcard: Postcard) {
            onInterrupt?.invoke(postcard)
        }
    })
}

/**
 * 添加map参数
 */
fun Postcard.wiMap(params: Map<String, Any?>? = null): Postcard {
    val bb = params?.toBundle()
    if (extras == null) {
        with(bb)
    } else {
        if (bb != null) {
            extras.putAll(bb)
        }
    }
    return this
}

/**
 * Intent里面的数据转换成map
 */
fun Intent.toMap(): Map<String, Any?> {
    return extras?.toMap() ?: emptyMap()
}

/**
 * Bundle里面的数据转换成map
 */
fun Bundle.toMap(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    keySet()?.forEach {
        map[it] = get(it)
    }
    return map
}

fun Map<String, Any?>.toBundle(): Bundle {
    val bundle = Bundle()
    this.forEach {
        if (it.value != null) {
            when (it.value) {
                is String -> bundle.putString(it.key, it.value as String)
                is Int -> bundle.putInt(it.key, it.value as Int)
                is Long -> bundle.putLong(it.key, it.value as Long)
                is Boolean -> bundle.putBoolean(it.key, it.value as Boolean)
                is Byte -> bundle.putByte(it.key, it.value as Byte)
                is Char -> bundle.putChar(it.key, it.value as Char)
                is Short -> bundle.putShort(it.key, it.value as Short)
                is Float -> bundle.putFloat(it.key, it.value as Float)
                is CharSequence -> bundle.putCharSequence(it.key, it.value as CharSequence)
                is Parcelable -> bundle.putParcelable(it.key, it.value as Parcelable)
                is Serializable -> bundle.putSerializable(it.key, it.value as Serializable)
                is Array<*> -> {
                    if ((it.value as Array<*>).isNotEmpty()) {
                        when ((it.value as Array<*>)[0]) {
                            is Parcelable -> bundle.putParcelableArray(it.key, it.value as Array<Parcelable>)
                            is CharSequence -> bundle.putCharSequenceArray(it.key, it.value as Array<CharSequence>)
                        }
                    }
                }
                is ArrayList<*> -> {
                    if ((it.value as ArrayList<*>).isNotEmpty()) {
                        when ((it.value as ArrayList<*>)[0]) {
                            is Parcelable -> bundle.putParcelableArrayList(it.key, it.value as ArrayList<Parcelable>)
                            is CharSequence -> bundle.putCharSequenceArrayList(it.key, it.value as ArrayList<CharSequence>)
                            is String -> bundle.putStringArrayList(it.key, it.value as ArrayList<String>)
                            is Int -> bundle.putIntegerArrayList(it.key, it.value as ArrayList<Int>)
                        }
                    }
                }
                is SparseArray<*> -> bundle.putSparseParcelableArray(it.key, it.value as SparseArray<Parcelable>)
                is ByteArray -> bundle.putByteArray(it.key, it.value as ByteArray)
                is ShortArray -> bundle.putShortArray(it.key, it.value as ShortArray)
                is CharArray -> bundle.putCharArray(it.key, it.value as CharArray)
                is FloatArray -> bundle.putFloatArray(it.key, it.value as FloatArray)
                is Bundle -> bundle.putBundle(it.key, it.value as Bundle)
                //其他对象 序列化成Json
                else -> {
                    val serializationService = ARouter.getInstance().navigation(SerializationService::class.java)
                    bundle.putString(it.key, serializationService.object2Json(it.value))
                }
            }
        }
    }
    return bundle
}