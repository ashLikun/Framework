package com.ashlikun.core.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.Exception

/**
 * 作者　　: 李坤
 * 创建时间: 2020/3/31　17:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：自己实现ViewModelProvider
 */
open class XViewModelProvider : ViewModelProvider {
    private val keys = HashMap<String, Class<*>>()

    constructor(owner: ViewModelStoreOwner, factory: Factory) : this(owner.viewModelStore, factory)
    constructor(store: ViewModelStore, factory: Factory) : super(store, factory)

    override fun <T : ViewModel> get(key: String, modelClass: Class<T>): T {
        if (!keys.containsKey(key)) {
            keys[key] = modelClass
        }
        return super.get(key, modelClass)
    }

    fun <T : ViewModel> get(key: String): T {
        if (keys.containsKey(key)) {
            return get(key, keys[key] as Class<ViewModel>) as T
        }
        throw Exception("请先调用get(key: String, modelClass: Class<T>)方法存入key与类型")
    }

    fun <T : ViewModel> forEach(block: (vm: T) -> Unit) {
        keys.forEach {
            block.invoke(get(it.key))
        }
    }
}