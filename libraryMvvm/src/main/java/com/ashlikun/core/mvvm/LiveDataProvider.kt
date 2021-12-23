package com.ashlikun.core.mvvm

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * 作者　　: 李坤
 * 创建时间: 2020/4/1　16:22
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：LiveData创建
 */
@Deprecated("直接创建")
class LiveDataProvider {
    private val maps: ConcurrentHashMap<String, MutableLiveData<*>> by lazy {
        //初始化集合(线程安全)
        ConcurrentHashMap<String, MutableLiveData<*>>()
    }

    /**
     * 通过指定的数据实体类获取对应的MutableLiveData类
     */
    @Deprecated("使用kclass")
    open operator fun <T> get(clazz: Class<T>): MutableLiveData<T> {
        return get(null, clazz)
    }

    /**
     * 通过指定的key或者数据实体类获取对应的MutableLiveData类
     */
    @Deprecated("使用kclass")
    open operator fun <T> get(key: String?, clazz: Class<T>): MutableLiveData<T> {
        var keyName: String =
            key ?: clazz.canonicalName ?: throw RuntimeException("livedata no key")
        var mutableLiveData: MutableLiveData<T>? = maps[keyName] as MutableLiveData<T>?
        //1.判断集合是否已经存在livedata对象
        if (mutableLiveData != null) {
            return mutableLiveData
        }
        //2.如果集合中没有对应实体类的Livedata对象，就创建并添加至集合中
        mutableLiveData = MutableLiveData()
        maps[keyName] = mutableLiveData
        return mutableLiveData
    }

    /**
     * 通过指定的数据实体类获取对应的MutableLiveData类
     */
    open operator fun <T : Any> get(clazz: KClass<T>): MutableLiveData<T> {
        return get(null, clazz)
    }

    /**
     * 通过指定的key或者数据实体类获取对应的MutableLiveData类
     */
    open operator fun <T : Any> get(key: String?, clazz: KClass<T>): MutableLiveData<T> {
        var keyName: String = key ?: clazz.simpleName ?: ""
        var mutableLiveData: MutableLiveData<T>? = maps[keyName] as MutableLiveData<T>?
        //1.判断集合是否已经存在livedata对象
        if (mutableLiveData != null) {
            return mutableLiveData
        }
        //2.如果集合中没有对应实体类的Livedata对象，就创建并添加至集合中
        mutableLiveData = MutableLiveData()
        maps[keyName] = mutableLiveData
        return mutableLiveData
    }

}