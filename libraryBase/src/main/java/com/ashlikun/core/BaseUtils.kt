package com.ashlikun.core

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.ashlikun.core.listener.IBaseWindow
import com.ashlikun.loadswitch.DefaultOnLoadLayoutListener
import com.ashlikun.loadswitch.OnLoadLayoutListener
import com.ashlikun.loadswitch.OnLoadSwitchClick
import java.lang.reflect.*
import kotlin.coroutines.CoroutineContext

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/19 19:53
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：工具
 */
object BaseUtils {
    /**
     * Activity onCreate创建之前
     */
    val onActivityPreCreated = mutableListOf<((activity: Activity, savedInstanceState: Bundle?) -> Unit)>()

    /**
     * Activity onAttachBaseContext
     */
    val onAttachBaseContext = mutableListOf<((context: Context) -> Context)>()

    /**
     * 当调用Activity的getResources将被调用，便于hook,只调用一次，内部会缓存
     */
    var onActivityGetResources = mutableListOf<((result: Resources) -> Resources)>()

    /**
     * 布局切换的布局渲染事件,必须有双参数构造方法
     */
    var switchLayoutListener: ((context: Context, click: OnLoadSwitchClick?) -> OnLoadLayoutListener?)? = null

    /**
     * ViewBinding反射的缓存
     */
    val viewBindingGetMap = mutableMapOf<Class<*>, AccessibleObject>()

    /**
     * 本框架协成默认错误的处理,如果调用者处理了，那么这里不会调用
     * CoroutineExceptionHandler { _, t ->
     * t.printStackTrace()
     * }
     */
    var coroutineExceptionHandler: CoroutineContext? = null


    /**
     *  获取设置的全局OnLoadLayoutListener
     */
    fun createSwitchLayoutListener(window: IBaseWindow) =
        createSwitchLayoutListener(window.requireContext, window.onLoadSwitchClick)

    /**
     * 获取设置的全局OnLoadLayoutListener
     */
    fun createSwitchLayoutListener(context: Context, click: OnLoadSwitchClick?) =
        switchLayoutListener?.invoke(context, click) ?: DefaultOnLoadLayoutListener(context, click)

    /**
     * 反射查找ViewBinding的view
     */
    fun getViewBindingView(obj: Any): View? {
        try {
            //检测是否有ViewBinding 库
            val viewBindingCls: Class<out ViewBinding> = ViewBinding::class.java
            val objCls: Class<*> = obj.javaClass
            //查找缓存
            var accessibleObject = viewBindingGetMap[objCls]
            if (accessibleObject != null) {
                accessibleObject.isAccessible = true
                if (accessibleObject is Method) {
                    return (accessibleObject.invoke(obj) as ViewBinding).root
                } else if (accessibleObject is Field) {
                    return (accessibleObject[obj] as ViewBinding).root
                }
            }
            var cls: Class<*>? = objCls
            while (cls != null && cls != Any::class.java) {
                val declaredFields = cls.declaredFields
                //获取字段
                for (f in declaredFields) {
                    if (viewBindingCls.isAssignableFrom(f.type)) {
                        f.isAccessible = true
                        val view = (f[obj] as ViewBinding).root
                        viewBindingGetMap[objCls] = f
                        return view
                    }
                }
                //获取方法
                val declaredMethods = cls.declaredMethods
                for (m in declaredMethods) {
                    if (viewBindingCls.isAssignableFrom(m.returnType)) {
                        m.isAccessible = true
                        val view = (m.invoke(obj) as ViewBinding).root
                        viewBindingGetMap[objCls] = m
                        return view
                    }
                }
                //获取父类的
                cls = cls.superclass
            }
        } catch (e: Exception) {
            return null
        } catch (e: NoClassDefFoundError) {
            return null
        }
        return null
    }

    /**
     * 获取1个参数的静态方法
     */
    @JvmStatic
    fun getViewToViewBindingClass(cls: Class<*>?, layoutInflater: LayoutInflater?): View? {
        val viewBindingToClass = getViewBindingToClass(cls, layoutInflater)
        try {
            if (viewBindingToClass != null) {
                if (viewBindingToClass is ViewBinding) {
                    return viewBindingToClass.root
                }
            }
        } catch (e: NoClassDefFoundError) {
            return null
        }
        return null
    }

    /**
     * 获取1个参数的静态方法
     *
     * @return ViewBinding
     */
    fun getViewBindingToClass(cls: Class<*>?, layoutInflater: LayoutInflater?): Any? {
        if (cls == null || layoutInflater == null) {
            return null
        }
        var isCache = false
        //从缓存获取
        try {
            var inflate: Method? = null
            val cacheValue = viewBindingGetMap[cls]
            if (cacheValue is Method) {
                isCache = true
                inflate = cacheValue
            }
            if (inflate != null) {
                inflate = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
            }
            //这里循环全部方法是为了混淆的时候无影响
            if (inflate == null) {
                val declaredMethods = cls.declaredMethods
                for (declaredMethod in declaredMethods) {
                    val modifiers = declaredMethod.modifiers
                    if (Modifier.isStatic(modifiers)) {
                        val parameterTypes = declaredMethod.parameterTypes
                        if (parameterTypes != null && parameterTypes.size == 3) {
                            if (LayoutInflater::class.java.isAssignableFrom(parameterTypes[0]) &&
                                ViewGroup::class.java.isAssignableFrom(parameterTypes[1]) &&
                                Boolean::class.javaPrimitiveType!!.isAssignableFrom(parameterTypes[2])
                            ) {
                                inflate = declaredMethod
                                break
                            }
                        }
                    }
                }
            }
            if (inflate != null) {
                //添加到缓存
                if (isCache) {
                    viewBindingGetMap[cls] = inflate
                }
                return inflate.invoke(null, layoutInflater)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}