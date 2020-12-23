package com.ashlikun.core.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 执行，常用于最外层
 * 无阻塞的
 */
inline fun <T> ViewModel.launch(context: CoroutineContext = EmptyCoroutineContext, delayTime: Long = 0, noinline job: suspend () -> T) = viewModelScope.launch(context) {
    try {
        delay(delayTime)
        job()
    } catch (ext: Exception) {
    }
}