package com.ashlikun.core.mvvm

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.ashlikun.core.BaseUtils
import com.ashlikun.core.activity.BaseActivity
import com.ashlikun.utils.other.coroutines.IODispatcher
import com.ashlikun.utils.other.coroutines.IoScope
import com.ashlikun.utils.other.coroutines.ThreadPoolDispatcher
import com.ashlikun.utils.ui.ActivityManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 */
inline fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return viewModelScope.launch(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun ViewModel.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return viewModelScope.launch(checkCoroutineExceptionHandler(IODispatcher + context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun ViewModel.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return viewModelScope.launch(checkCoroutineExceptionHandler(ThreadPoolDispatcher + context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> ViewModel.async(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> T
): Deferred<T> {
    return viewModelScope.async(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 */
inline fun LifecycleOwner.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return lifecycleScope.launch(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun LifecycleOwner.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return lifecycleScope.launch(checkCoroutineExceptionHandler(IODispatcher + context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun LifecycleOwner.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return lifecycleScope.launch(checkCoroutineExceptionHandler(ThreadPoolDispatcher + context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> LifecycleOwner.async(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> T
): Deferred<T> {
    return lifecycleScope.async(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 */
inline fun View.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = findViewTreeLifecycleOwner()!!.launch(context, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun View.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = findViewTreeLifecycleOwner()!!.launchIO(context, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun View.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = findViewTreeLifecycleOwner()!!.launchThreadPoll(context, delayTime, job)

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> View.async(
    context: CoroutineContext = EmptyCoroutineContext,
    delayTime: Long = 0,
    noinline job: suspend () -> T
) = findViewTreeLifecycleOwner()!!.async(context, delayTime, job)

inline fun checkCoroutineExceptionHandler(context: CoroutineContext): CoroutineContext {
    var ct = context
//    if (context[CoroutineExceptionHandler.Key] == null) {
//    CoroutineExceptionHandler { _, t ->
//        t.printStackTrace()
//    }
    //如果沒有处理错误的时候
    if (context[CoroutineExceptionHandler.Key] == null) {
        if (BaseUtils.coroutineExceptionHandler != null && BaseUtils.coroutineExceptionHandler is CoroutineExceptionHandler) {
            ct = context + BaseUtils.coroutineExceptionHandler as CoroutineExceptionHandler
        }
    }
//    }
    return ct
}

/**
 * 获取Fragment
 */
inline fun <reified T : BaseMvvmFragment<*>> FragmentActivity.mvvmFragment(): T? {
    return (supportFragmentManager?.fragments?.find { T::class.java.isAssignableFrom(it.javaClass) } as? T)
}

/**
 * 获取Fragment
 */
inline fun <reified T : BaseMvvmFragment<*>> FragmentActivity.baseFr(): T? {
    return (supportFragmentManager?.fragments?.find { T::class.java.isAssignableFrom(it.javaClass) } as? T)
}

/**
 * 获取前台 Activity
 */
inline val fBaseActivity: BaseActivity?
    get() = ActivityManager.get().getTagActivity(BaseActivity::class.java)
inline val fFActivity: FragmentActivity?
    get() = ActivityManager.get().getTagActivity(FragmentActivity::class.java)
