package com.ashlikun.core.mvvm

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.ashlikun.core.BaseUtils
import com.ashlikun.core.activity.BaseActivity
import com.ashlikun.utils.other.coroutines.CException
import com.ashlikun.utils.other.coroutines.IODispatcher
import com.ashlikun.utils.other.coroutines.ThreadPoolDispatcher
import com.ashlikun.utils.ui.ActivityManager
import com.ashlikun.utils.ui.extend.lifecycle
import com.ashlikun.utils.ui.extend.toLifecycle
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2)) {
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(
        checkCoroutineExceptionHandler(
            IODispatcher + context,
            exception = cache,
            exception2 = cache2,
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(
        checkCoroutineExceptionHandler(
            ThreadPoolDispatcher + context,
            exception = cache,
            exception2 = cache2,
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> T
): Deferred<T> {
    val handleContext = checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2)
    return viewModelScope.async(handleContext) {
        delay(delayTime)
        //自己实现异常，防止根异常无法捕获
        runCatching {
            job()
        }.onFailure {
            if (handleContext is CoroutineExceptionHandler) handleContext.handleException(coroutineContext, it)
        }.getOrNull() as T
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2)) {
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(
        checkCoroutineExceptionHandler(
            IODispatcher + context,
            exception = cache,
            exception2 = cache2,
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(
        checkCoroutineExceptionHandler(
            ThreadPoolDispatcher + context,
            exception = cache,
            exception2 = cache2,
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> T
): Deferred<T> {
    val handleContext = checkCoroutineExceptionHandler(context, exception = cache, exception2 = cache2)
    return lifecycleScope.async(handleContext) {
        delay(delayTime)
        //自己实现异常，防止根异常无法捕获
        runCatching {
            job()
        }.onFailure {
            if (handleContext is CoroutineExceptionHandler) handleContext.handleException(coroutineContext, it)
        }.getOrNull() as T
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
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
) = toLifecycle().launch(context, cache, cache2, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun View.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
) = toLifecycle().launchIO(context, cache, cache2, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun View.launchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
) = toLifecycle().launchThreadPoll(context, cache, cache2, delayTime, job)

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> View.async(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> T
) = toLifecycle().async(context, cache, cache2, delayTime, job)

/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 * 自动等待Lifecycle
 * @param onStart 执行返回的Job
 */
inline fun View.launchLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline onStart: ((Job) -> Unit)? = null,
    noinline job: suspend CoroutineScope.() -> Unit
) = lifecycle {
    val jobR = it.launch(context, cache, cache2, delayTime, job)
    onStart?.invoke(jobR)
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 * 自动等待Lifecycle
 * @param onStart 执行返回的Job
 */
inline fun View.launchIOLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline onStart: ((Job) -> Unit)? = null,
    noinline job: suspend CoroutineScope.() -> Unit
) = lifecycle {
    val jobR = it.launchIO(context, cache, cache2, delayTime, job)
    onStart?.invoke(jobR)
}

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 * 自动等待Lifecycle
 * @param onStart 执行返回的Job
 */
inline fun View.launchThreadPollLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline onStart: ((Job) -> Unit)? = null,
    noinline job: suspend CoroutineScope.() -> Unit
) = lifecycle {
    val jobR = it.launchThreadPoll(context, cache, cache2, delayTime, job)
    onStart?.invoke(jobR)
}

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 * 自动等待Lifecycle
 * @param onStart 执行返回的Job
 */
inline fun <T> View.asyncLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline onStart: ((Job) -> Unit)? = null,
    noinline job: suspend CoroutineScope.() -> T
) = lifecycle {
    val jobR = it.async(context, cache, cache2, delayTime, job)
    onStart?.invoke(jobR)
}

/**
 * 执行，常用于最外层,主线程Dispatchers.Main
 * 无阻塞的
 */
inline fun activityLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
) = fCActivity?.launch(context, cache, cache2, delayTime, job)

/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun activityLaunchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
) = fCActivity?.launchIO(context, cache, cache2, delayTime, job)
/**
 * 执行，在Android IO线程中执行，可以用于最外层 Dispatchers.IO 线程 无阻塞的
 */
inline fun activityLaunchThreadPoll(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> Unit
) = fCActivity?.launchThreadPoll(context, cache, cache2, delayTime, job)

/**
 * 异步执行，常用于最外层
 * 多个 async 任务是并行的
 * 特点带返回值 async 返回的是一个Deferred<T>，需要调用其await()方法获取结果。
 */
inline fun <T> activityAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline cache: ((Throwable) -> Unit)? = null,
    noinline cache2: ((CoroutineContext, Throwable) -> Unit)? = null,
    delayTime: Long = 0,
    noinline job: suspend CoroutineScope.() -> T
) = fCActivity?.async(context, cache, cache2, delayTime, job)

inline fun checkCoroutineExceptionHandler(
    context: CoroutineContext,
    isAddDefault: Boolean = true,
    noinline exception: ((Throwable) -> Unit)? = null,
    noinline exception2: ((CoroutineContext, Throwable) -> Unit)? = null,
): CoroutineContext {
    var ct = context
    if (exception != null) ct += CException(exception)
    if (exception2 != null) ct += CException(exception2)
    if (isAddDefault && ct[CoroutineExceptionHandler.Key] == null) {
        if (BaseUtils.coroutineExceptionHandler != null) {
            ct += BaseUtils.coroutineExceptionHandler!!
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
