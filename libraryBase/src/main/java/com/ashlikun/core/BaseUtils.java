package com.ashlikun.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.ashlikun.core.listener.IBaseWindow;
import com.ashlikun.loadswitch.DefaultOnLoadLayoutListener;
import com.ashlikun.loadswitch.OnLoadLayoutListener;
import com.ashlikun.loadswitch.OnLoadSwitchClick;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    /**
     * 本框架协成默认错误的处理,如果调用者处理了，那么这里不会调用
     * CoroutineExceptionHandler { _, t ->
     * t.printStackTrace()
     * }
     */
    public static Object coroutineExceptionHandler;


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
            AccessibleObject accessibleObject = null;
            if (viewBindingGetMap != null) {
                accessibleObject = viewBindingGetMap.get(objCls);
            }
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
                        if (viewBindingGetMap == null) {
                            viewBindingGetMap = new HashMap<>();
                        }
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
                        if (viewBindingGetMap == null) {
                            viewBindingGetMap = new HashMap<>();
                        }
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

    /**
     * 获取1个参数的静态方法
     */
    public static View getViewToViewBindingClass(Class cls, LayoutInflater layoutInflater) {
        Object viewBindingToClass = getViewBindingToClass(cls, layoutInflater);
        try {
            if (viewBindingToClass != null) {
                if (viewBindingToClass instanceof ViewBinding) {
                    ViewBinding bb = (ViewBinding) viewBindingToClass;
                    return bb.getRoot();
                }
            }
        } catch (NoClassDefFoundError e) {
            return null;
        }
        return null;
    }

    /**
     * 获取1个参数的静态方法
     *
     * @return ViewBinding
     */
    public static Object getViewBindingToClass(Class cls, LayoutInflater layoutInflater) {
        if (cls == null || layoutInflater == null) {
            return null;
        }
        boolean isCache = false;
        //从缓存获取
        try {
            Method inflate = null;
            if (viewBindingGetMap != null) {
                AccessibleObject aa = viewBindingGetMap.get(cls);
                if (aa instanceof Method) {
                    isCache = true;
                    inflate = (Method) aa;
                }
            }
            if (inflate != null) {
                inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            }
            //这里循环全部方法是为了混淆的时候无影响
            if (inflate == null) {
                Method[] declaredMethods = cls.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    int modifiers = declaredMethod.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                        if (parameterTypes != null && parameterTypes.length == 3) {
                            if (LayoutInflater.class.isAssignableFrom(parameterTypes[0]) &&
                                    ViewGroup.class.isAssignableFrom(parameterTypes[1]) &&
                                    boolean.class.isAssignableFrom(parameterTypes[2])) {
                                inflate = declaredMethod;
                                break;
                            }
                        }
                    }
                }
            }
            if (inflate != null) {
                //添加到缓存
                if (isCache) {
                    if (viewBindingGetMap == null) {
                        viewBindingGetMap = new HashMap<>();
                    }
                    viewBindingGetMap.put(cls, inflate);
                }
                return inflate.invoke(null, layoutInflater);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
