package com.ashlikun.core.mvvm

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.ashlikun.core.BaseUtils
import com.ashlikun.core.activity.BaseActivity
import com.ashlikun.utils.other.coroutines.*
import com.ashlikun.utils.ui.ActivityManager
import com.ashlikun.utils.ui.extend.lifecycle
import com.ashlikun.utils.ui.fCActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 */
inline fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return viewModelScope.launch(checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2, exception3 = cache3)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun ViewModel.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return viewModelScope.launch(
        checkCoroutineExceptionHandler(
            IODispatcher + context,
            exception = cache,
            exception2 = cache2,
            exception3 = cache3
        )
    ) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun ViewModel.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return viewModelScope.launch(
        checkCoroutineExceptionHandler(
            ThreadPoolDispatcher + context,
            exception = cache,
            exception2 = cache2,
            exception3 = cache3
        )
    ) {
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
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> T
): Deferred<T> {
    return viewModelScope.async(checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2, exception3 = cache3)) {
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
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return lifecycleScope.launch(checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2, exception3 = cache3)) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun LifecycleOwner.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return lifecycleScope.launch(
        checkCoroutineExceptionHandler(
            IODispatcher + context,
            exception = cache,
            exception2 = cache2,
            exception3 = cache3
        )
    ) {
        delay(delayTime)
        job()
    }
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun LifecycleOwner.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
): Job {
    return lifecycleScope.launch(
        checkCoroutineExceptionHandler(
            ThreadPoolDispatcher + context,
            exception = cache,
            exception2 = cache2,
            exception3 = cache3
        )
    ) {
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
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> T
): Deferred<T> {
    return lifecycleScope.async(checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2, exception3 = cache3)) {
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
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = lifecycle {
    it.launch(context, cache, cache2, cache3, delayTime, job)
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun View.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = lifecycle {
    it.launchIO(context, cache, cache2, cache3, delayTime, job)
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun View.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = lifecycle {
    it.launchThreadPoll(context, cache, cache2, cache3, delayTime, job)
}

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> View.async(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> T
) = lifecycle {
    it.async(context, cache, cache2, cache3, delayTime, job)
}

/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 */
inline fun activityLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = fCActivity?.launch(context, cache, cache2, cache3, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun activityLaunchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = fCActivity?.launchIO(context, cache, cache2, cache3, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun activityLaunchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> Unit
) = fCActivity?.launchThreadPoll(context, cache, cache2, cache3, delayTime, job)

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> activityAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline cache3: (() -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend () -> T
) = fCActivity?.async(context, cache, cache2, cache3, delayTime, job)

inline fun checkCoroutineExceptionHandler(
    context: CoroutineContext,
    isAddDefault: Boolean = true,
    noinline exception: ((Throwable) -> Unit)? = null,
    noinline exception2: ((CoroutineContext, Throwable) -> Unit)? = null,
    noinline exception3: (() -> Unit)? = null
): CoroutineContext {
    var ct = context
    if (exception != null) ct = context + CException(exception)
    if (exception2 != null) ct = context + CException(exception2)
    if (exception3 != null) ct = context + CException(exception3)
    if (isAddDefault && context[CoroutineExceptionHandler.Key] == null) {
        if (BaseUtils.coroutineExceptionHandler != null) {
            ct = context + defaultCoroutineExceptionHandler
        }
    }
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
