package com.ashlikun.core.mvvm

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 执行，常用于最外层
 * 无阻塞的
 */
inline fun <T> ViewModel.launch(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Job {
    var ct = context
    if (ct !is CoroutineExceptionHandler) {
        ct = context + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        }
    }
    return viewModelScope.launch(ct) {
        delay(delayTime)
        job()
    }
}

val newInstanceFactory by lazy { ViewModelProvider.NewInstanceFactory() }
inline fun <T> ComponentActivity.launch(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Job {
    val viewModelScope = ViewModelProvider(this, newInstanceFactory).get(ViewModelScope::class.java)
    return viewModelScope.launch(context, delayTime, job)
}

inline fun <T> Fragment.launch(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T): Job {
    val viewModelScope = ViewModelProvider(this, newInstanceFactory).get(ViewModelScope::class.java)
    return viewModelScope.launch(context, delayTime, job)
}

internal class ViewModelScope() : ViewModel() {

}
