package com.ashlikun.core.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ashlikun.core.BaseUtils;
import com.ashlikun.core.R;
import com.ashlikun.core.activity.BaseActivity;
import com.ashlikun.core.listener.IBaseWindow;
import com.ashlikun.core.listener.OnDispatcherMessage;
import com.ashlikun.loadswitch.ContextData;
import com.ashlikun.loadswitch.LoadSwitch;
import com.ashlikun.loadswitch.LoadSwitchService;
import com.ashlikun.loadswitch.OnLoadSwitchClick;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.supertoobar.SuperToolBar;
import com.ashlikun.utils.ui.StatusBarCompat;
import com.ashlikun.utils.ui.UiUtils;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/8 15:56
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：自定义最顶层的Fragment
 * @see IBaseWindow : 只要是窗口都会实现这个统一接口
 * @see OnDispatcherMessage : Fragment处理其他(Activity)发送的事件
 */

public abstract class BaseFragment extends Fragment implements IBaseWindow, OnDispatcherMessage {
    /**
     * 请求CODE
     */

    public int REQUEST_CODE = Math.abs(this.getClass().getSimpleName().hashCode() % 60000);

    /**
     * 宿主activity
     */


    protected Activity activity;
    protected Context context;
    /**
     * 布局
     */
    protected View rootView;

    /**
     * 布局切换
     */
    public LoadSwitchService switchService = null;

    /**
     * 是否是回收利用的Fragment
     */
    protected boolean isRecycle = false;

    protected SuperToolBar toolbar;
    protected View switchRoot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = activity;
        Intent intent = new Intent();
        if (getArguments() != null) {
            intent.putExtras(getArguments());
        }
        parseIntent(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            isRecycle = false;
            setContentView();
        } else {
            isRecycle = true;
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    /**
     * 设置布局，可以重写
     */
    protected void setContentView() {
        int layoutId = getLayoutId();
        if (layoutId != View.NO_ID) {
            rootView = UiUtils.getInflaterView(activity, getLayoutId());
        } else {
            rootView = getContentView();
        }
    }

    @Override
    public void initLoadSwitch() {
        View viewSwitch = getSwitchRoot();
        if (viewSwitch != null) {
            switchService = LoadSwitch.get()
                    .register(viewSwitch, BaseUtils.getSwitchLayoutListener(getContext(), this));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isRecycle) {
            baseInitView();
            initView();
            initData();
        }
    }


    /**
     * 解析意图
     */
    protected void parseIntent(Intent intent) {
        try {
            ARouter.getInstance().inject(this);
        } catch (Exception e) {
        }
    }

    protected void baseInitView() {
        toolbar = f(R.id.toolbar);
        switchRoot = f(R.id.switchRoot);
        initLoadSwitch();
    }

    @Override
    public <T extends View> T f(@IdRes int id) {
        return rootView.findViewById(id);
    }

    public BaseActivity getActivitySupper() {
        if (activity instanceof BaseActivity) {
            return ((BaseActivity) activity);
        } else {
            return null;
        }
    }

    public StatusBarCompat getActivityStatusBar() {
        return getActivitySupper().getStatusBar();
    }


    /**
     * 显示不同的界面布局 监听器
     */
    @Override
    public OnLoadSwitchClick getOnLoadSwitchClick() {
        if (this instanceof OnLoadSwitchClick) {
            return (OnLoadSwitchClick) this;
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
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 当activity返回的时候,需要配合activity调用
     *
     * @return false:默认不处理  true:fragment处理
     */

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        cancelAllHttp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAllHttp();
    }

    @Override
    public void showLoading(ContextData data) {
        if (switchService != null) {
            switchService.showLoading(data);
        }
    }

    @Override
    public LoadSwitchService getSwitchService() {
        return switchService;
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
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    /**
     * 销毁网络访问
     */
    @Override
    public void cancelAllHttp() {
        OkHttpUtils.getInstance().cancelTag(this);
    }

    /**
     * 发送事件给activity
     *
     * @param what:事件类型
     * @param bundle    事件传递的数据
     */
    public void sendMsgToActivity(int what, Bundle bundle) {
        BaseActivity activity = getActivitySupper();
        if (activity != null) {
            activity.onDispatcherMessage(what, bundle);
        }
    }

    /**
     * 处理Activity发送过来的事件
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
}
