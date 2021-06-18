package com.ashlikun.core;

import android.content.Context;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.ashlikun.core.listener.IBaseWindow;
import com.ashlikun.loadswitch.DefaultOnLoadLayoutListener;
import com.ashlikun.loadswitch.OnLoadLayoutListener;
import com.ashlikun.loadswitch.OnLoadSwitchClick;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/5/24　14:43
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：工具
 */
public class BaseUtils {
    /**
     * 布局切换的布局渲染事件,必须有双参数构造方法
     */
    public static Class<? extends OnLoadLayoutListener> switchLayoutListener = null;
    private static HashMap<Class, AccessibleObject> viewBindingGetMap;


    public static void init(Class<? extends OnLoadLayoutListener> listener) {
        switchLayoutListener = listener;
    }

    /**
     * 通过反射 获取设置的全局OnLoadLayoutListener
     *
     * @param context
     * @param window
     * @return
     */
    public static OnLoadLayoutListener getSwitchLayoutListener(Context context, IBaseWindow window) {
        OnLoadLayoutListener loadLayoutListener = null;
        if (switchLayoutListener != null) {
            try {
                //获取构造函数
                Constructor con = switchLayoutListener.getConstructor(Context.class, OnLoadSwitchClick.class);
                loadLayoutListener = (OnLoadLayoutListener) con.newInstance(context, window.getOnLoadSwitchClick());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loadLayoutListener == null) {
                try {
                    loadLayoutListener = (OnLoadLayoutListener) switchLayoutListener.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (loadLayoutListener == null) {
            loadLayoutListener = new DefaultOnLoadLayoutListener(context, window.getOnLoadSwitchClick());
        }
        return loadLayoutListener;
    }

    /**
     * 获取设置的全局OnLoadLayoutListener
     *
     * @param context
     * @return
     */
    public static OnLoadLayoutListener getSwitchLayoutListener(Context context, OnLoadSwitchClick click) {
        OnLoadLayoutListener loadLayoutListener = null;
        if (switchLayoutListener != null) {
            try {
                //获取构造函数
                Constructor con = switchLayoutListener.getConstructor(Context.class, OnLoadSwitchClick.class);
                loadLayoutListener = (OnLoadLayoutListener) con.newInstance(context, click);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loadLayoutListener == null) {
                try {
                    loadLayoutListener = (OnLoadLayoutListener) switchLayoutListener.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (loadLayoutListener == null) {
            loadLayoutListener = new DefaultOnLoadLayoutListener(context, click);
        }
        return loadLayoutListener;
    }

    /**
     * 反射查找ViewBinding的view
     */
    public static View getViewBindingView(Object object) {
        if (object == null) {
            return null;
        }
        try {
            //检测是否有ViewBinding 库
            Class viewBindingCls = ViewBinding.class;
            if (viewBindingGetMap == null) {
                viewBindingGetMap = new HashMap<>();
            }
            Class objCls = object.getClass();
            //查找缓存
            AccessibleObject accessibleObject = viewBindingGetMap.get(objCls);
            if (accessibleObject != null) {
                accessibleObject.setAccessible(true);
                if (accessibleObject instanceof Method) {
                    return ((ViewBinding) ((Method) accessibleObject).invoke(object)).getRoot();
                } else if (accessibleObject instanceof Field) {
                    return ((ViewBinding) ((Field) accessibleObject).get(object)).getRoot();
                }
            }
            Class cls = objCls;
            while (cls != null && cls != Object.class) {
                Field[] declaredFields = cls.getDeclaredFields();
                //获取字段
                for (Field f : declaredFields) {
                    if (viewBindingCls.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        View view = ((ViewBinding) f.get(object)).getRoot();
                        viewBindingGetMap.put(objCls, f);
                        return view;
                    }
                }
                //获取方法
                Method[] declaredMethods = cls.getDeclaredMethods();
                for (Method m : declaredMethods) {
                    if (viewBindingCls.isAssignableFrom(m.getReturnType())) {
                        m.setAccessible(true);
                        View view = ((ViewBinding) m.invoke(object)).getRoot();
                        viewBindingGetMap.put(objCls, m);
                        return view;
                    }
                }
                //获取父类的
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
            return null;
        } catch (NoClassDefFoundError e) {
            return null;
        }
        return null;
    }

}
