package com.ashlikun.core.mvvm

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 执行，常用于最外层
 * 无阻塞的
 */
inline fun <T> ViewModel.launch(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Job {
    return viewModelScope.launch(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

inline fun <T> ViewModel.async(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Deferred<T> {
    return viewModelScope.async(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

inline fun <T> LifecycleOwner.launch(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Job {
    return lifecycleScope.launch(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

inline fun <T> LifecycleOwner.async(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Deferred<T> {
    return lifecycleScope.async(checkCoroutineExceptionHandler(context)) {
        delay(delayTime)
        job()
    }
}

inline fun checkCoroutineExceptionHandler(context: CoroutineContext): CoroutineContext {
    var ct = context
    if (context[CoroutineExceptionHandler.Key] == null) {
        ct = context + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        }
    }
    return ct
}
