package com.ashlikun.core.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * 作者　　: 李坤
 * 创建时间: 2020/3/31　17:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：自己实现ViewModelProvider
 */
open class XViewModelProvider(store: ViewModelStore, var factory: Factory) :
    ViewModelProvider(store, factory) {
    private val keys = HashMap<String, Class<out ViewModel>>()

    constructor(owner: ViewModelStoreOwner, factory: Factory) : this(owner.viewModelStore, factory)

    override fun <T : ViewModel> get(key: String, modelClass: Class<T>): T {
        if (!keys.containsKey(key)) {
            keys[key] = modelClass
        }
        val model = super.get(key, modelClass)
        //是否重新创建
        if (factory is ViewModelFactoryImp) {
            (factory as ViewModelFactoryImp).isReCreate(model)
        }
        return model
    }

    fun <T : ViewModel> get(key: String): T {
        if (keys.containsKey(key)) {
            return get(key, keys[key] as Class<ViewModel>) as T
        }
        throw Exception("请先调用get(key: String, modelClass: Class<T>)方法存入key与类型")
    }

    fun <T : ViewModel> forEach(block: (vm: T) -> Unit) {
        keys.forEach {
            block.invoke(get(it.key, it.value) as T)
        }
    }
}