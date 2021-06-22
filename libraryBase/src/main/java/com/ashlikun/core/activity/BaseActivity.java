package com.ashlikun.core.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ashlikun.core.BaseUtils;
import com.ashlikun.core.R;
import com.ashlikun.core.listener.IBaseWindow;
import com.ashlikun.core.listener.OnDispatcherMessage;
import com.ashlikun.loadswitch.ContextData;
import com.ashlikun.loadswitch.LoadSwitch;
import com.ashlikun.loadswitch.LoadSwitchService;
import com.ashlikun.loadswitch.OnLoadSwitchClick;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.supertoobar.SuperToolBar;
import com.ashlikun.utils.bug.BugUtils;
import com.ashlikun.utils.ui.StatusBarCompat;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/8 15:56
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：自定义最顶层的Activity
 * @see IBaseWindow : 只要是窗口都会实现这个统一接口
 * @see OnDispatcherMessage : Activity处理其他(Fragment)发送的事件
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseWindow, OnDispatcherMessage {

    /**
     * 请求CODE
     */
    public int REQUEST_CODE = Math.abs(this.getClass().getSimpleName().hashCode() % 60000);

    /**
     * 布局切换
     */
    public LoadSwitchService switchService = null;
    protected SuperToolBar toolbar;
    protected View switchRoot;
    protected StatusBarCompat statusBar;
    /**
     * ViewBinding的Class
     */
    protected Class viewBindingClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BugUtils.orientationBug8_0(this);
        super.onCreate(savedInstanceState);
        if (getIntent() == null) {
            setIntent(new Intent());
        }
        parseIntent(getIntent());
        setActivityContentView();
        setStatueBar();
        baseInitView();
        initView();
        initData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) {
            intent = new Intent();
        }
        setIntent(intent);
        parseIntent(intent);
        newData();
    }

    /**
     * 设置状态栏
     */
    protected void setStatueBar() {
        if (isStatusBarEnable()) {
            statusBar = new StatusBarCompat(this);
            //设置状态栏颜色兼容,默认只是颜色
            if (isStatusTranslucent() || isStatusTranslucentAndroidMHalf()) {
                statusBar.translucentStatusBar(isStatusTranslucentAndroidMHalf());
            } else {
                statusBar.setStatusBarColor(getStatusBarColor());
            }
        }
    }


    /**
     * 设置activity的布局，可以重写
     */
    protected void setActivityContentView() {
        int layoutId = getLayoutId();
        if (layoutId != View.NO_ID) {
            setContentView(layoutId);
        } else {
            View view = getContentView();
            if (view != null) {
                setContentView(view);
            } else {
                //通过反射获取ViewBinding
                if (viewBindingClass != null) {
                    view = BaseUtils.getViewToViewBindingClass(viewBindingClass, getLayoutInflater());
                }
                if (view == null) {
                    view = BaseUtils.getViewBindingView(this);
                }
                if (view != null) {
                    setContentView(view);
                }
            }
        }
    }

    /**
     * 基本的View初始化
     */
    protected void baseInitView() {
        toolbar = f(R.id.toolbar);
        switchRoot = f(R.id.switchRoot);
        initLoadSwitch();
    }

    @Override
    public <T extends View> T f(@IdRes int id) {
        return findViewById(id);
    }

    public StatusBarCompat getStatusBar() {
        return statusBar;
    }

    /**
     * 初始化布局切换的管理器
     */
    @Override
    public void initLoadSwitch() {
        View view = getSwitchRoot();
        if (view != null) {
            switchService = LoadSwitch.get()
                    .register(view, BaseUtils.getSwitchLayoutListener(this, this));
        }
    }


    /**
     * onNewIntent 触发的调用
     */
    protected void newData() {

    }

    /**
     * ：解析意图
     */
    protected void parseIntent(@NonNull Intent intent) {
        try {
            ARouter.getInstance().inject(this);
        } catch (Exception e) {

        }
    }


    /**
     * 获取状态栏颜色
     */
    public int getStatusBarColor() {
        //获取主题颜色
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{R.attr.statusColorCustom});
        int color = array.getColor(0, 0xffffffff);
        array.recycle();
        return color;
    }

    /**
     * 内容是不是放到状态栏里面
     */
    public boolean isStatusTranslucent() {
        return false;
    }

    /**
     * 6.0以下是否绘制半透明,因为不能设置状态栏字体颜色
     */

    public boolean isStatusTranslucentAndroidMHalf() {
        return false;
    }

    /**
     * 状态栏是否开启沉浸式
     */
    public boolean isStatusBarEnable() {
        return true;
    }


    /**
     * 显示不同的界面布局 监听器
     */
    @Override
    public OnLoadSwitchClick getOnLoadSwitchClick() {
        if (this instanceof OnLoadSwitchClick) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * 获取需要转化为{@link LoadSwitchService}的控件
     */
    @Override
    public View getSwitchRoot() {
        return switchRoot;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAllHttp();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        cancelAllHttp();
    }

    @Override
    public LoadSwitchService getSwitchService() {
        return switchService;
    }

    @Override
    public void showLoading(ContextData data) {
        if (switchService != null) {
            switchService.showLoading(data);
        }
    }

    @Override
    public void showContent() {
        if (switchService != null) {
            switchService.showContent();
        }
    }

    @Override
    public void showEmpty(ContextData data) {
        if (switchService != null) {
            switchService.showEmpty(data);
        }
    }

    @Override
    public void showRetry(ContextData data) {
        if (switchService != null) {
            switchService.showRetry(data);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    /**
     * 销毁网络访问
     */
    @Override
    public void cancelAllHttp() {
        OkHttpUtils.getInstance().cancelTag(this);
    }

    /**
     * 处理fragment发送过来的事件
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    @Override
    public void onDispatcherMessage(int what, Bundle bundle) {

    }

    /**
     * 处理fragment发送过来的数据
     *
     * @param what:事件类型
     * @param data      事件传递的数据
     */
    @Override
    public void onDispatcherMessage(int what, Object data) {

    }


    /**
     * 提供数据给Fragment
     *
     * @param what:事件类型
     * @return 事件传递的数据
     */
    @Override
    public <T> T getDispatcherMessage(int what) {
        return null;
    }

    public boolean getUserVisibleHint() {
        return true;
    }
}
