package com.ashlikun.core.mvvm

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * @author　　: 李坤
 * 创建时间: 2020/3/27 17:56
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：创建ViewModel的工厂类
 * @param mvvmBaseInterface 一般是[BaseMvvmActivity]或者[BaseMvvmFragment]
 */

open class ViewModelFactoryImp(var mvvmBaseInterface: MvvmBaseInterface? = null) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val model = super.create(modelClass)
        initModel(model)
        return model
    }

    /**
     * 宿主是否重新创建了,与initModel方法对应
     * true:重新创建了
     * false:第一次创建
     */
    fun <T> isReCreate(model: T): Boolean {
        if (model is BaseViewModel) {
            //context
            var result = when {
                model.context != null -> {
                    val context: Context? = when (mvvmBaseInterface) {
                        is Context -> mvvmBaseInterface as Context
                        is Fragment -> (mvvmBaseInterface as Fragment).context
                        else -> null
                    }
                    context != model.context
                }
                model.lifecycleOwner != null -> mvvmBaseInterface != model.lifecycleOwner
                else -> model.isDestroy
            }
            if (result) {
                //清空对宿主的引用
                model.isDestroy = false
                model.context = null
                model.lifecycleOwner = null
                model.loadSwitchService = null
                initModel(model)
            }
            return result
        }
        return false
    }

    /**
     * 初始化公共的资源
     */
    fun <T> initModel(model: T) {
        if (model is BaseViewModel) {
            //vm保存context
            model.context = when (mvvmBaseInterface) {
                is Context -> mvvmBaseInterface as Context
                is Fragment -> (mvvmBaseInterface as Fragment).context
                else -> null
            }
            if (mvvmBaseInterface is LifecycleOwner) {
                model.lifecycleOwner = mvvmBaseInterface as LifecycleOwner
            }
            //处理vm的公共事件
            mvvmBaseInterface?.handeBaseEventer(model)
            //vm监听Activity或者Fragment的生命周期
            model.addObserver.value = model
        }
    }

    companion object {
        /**
         * 获取ViewModel注解
         */
        fun <VM : BaseViewModel> getViewModelAnnotation(viewClazz: Class<*>) =
            (viewClazz.getAnnotation(IViewModel::class.java)?.value as KClass<VM>?)?.java

        /**
         * 获取父类的泛型BaseViewModel
         */
        fun <VM : BaseViewModel> getViewModelParameterizedType(viewClazz: Class<*>): Class<VM>? {
            if (viewClazz.genericSuperclass is ParameterizedType && (viewClazz.genericSuperclass as ParameterizedType).actualTypeArguments.isNotEmpty()) {
                val modelClass2 =
                    (viewClazz.genericSuperclass as ParameterizedType).actualTypeArguments[0]
                if (modelClass2 is Class<*>?) {
                    return modelClass2 as Class<VM>?
                }
            }
            return null
        }
    }
}