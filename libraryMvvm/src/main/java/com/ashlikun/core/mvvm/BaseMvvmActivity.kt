package com.ashlikun.core.mvvm

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ashlikun.core.activity.BaseActivity
import com.ashlikun.loadswitch.LoadSwitchService


/**
 * 作者　　: 李坤
 * 创建时间: 2020/3/27　17:22
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：MVVM 架构的基础Activity
 */
open abstract class BaseMvvmActivity<VM : BaseViewModel>
    : BaseActivity(), MvvmBaseInterface {
    /**
     * 管理BaseViewModel
     */
    val viewModelProvider: XViewModelProvider by lazy {
        XViewModelProvider(this, ViewModelFactoryImp(this))
    }
    val viewModel: VM by lazy {
        //获取注解
        var modelClass: Class<VM>? = ViewModelFactoryImp.getViewModelAnnotation(javaClass)
            ?: ViewModelFactoryImp.getViewModelParameterizedType(javaClass)
            ?: throw RuntimeException("ViewModel创建失败!检查是否声明了@ViewModel(XXX.class)注解  或者 从写initViewModel方法 或者当前View的泛型没用ViewModel")
        //初始化Main的ViewModel
        viewModelProvider[modelClass!!]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //主动初始化
        viewModel.dataInit
        initViewModel()
        super.onCreate(savedInstanceState)
    }

    override fun baseInitView() {
        super.baseInitView()
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                it.loadSwitchService = switchService
            }
        }
    }

    override fun parseIntent(intent: Intent) {
        super.parseIntent(intent)
        if (intent != null) {
            viewModelProvider.forEach<ViewModel> {
                if (it is BaseViewModel) {
                    it.parseIntent(intent)
                }
            }
        }
    }

    /**
     * onNewIntent 触发的调用
     */
    override fun newData() {
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                it.newData()
            }
        }
    }

    /**
     * 这里可以对其他的View
     */
    protected open fun initViewModel() {


    }

    override fun onDispatcherMessage(what: Int, bundle: Bundle) {
        super.onDispatcherMessage(what, bundle)
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                it.onDispatcherMessage(what, bundle)
            }
        }
    }

    /**
     * 处理fragment发送过来的数据
     *
     * @param what:事件类型
     * @param data      事件传递的数据
     */
    override fun onDispatcherMessage(what: Int, data: Any) {
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                it.onDispatcherMessage(what, data)
            }
        }
    }

    override fun <T : Any?> getDispatcherMessage(what: Int): T? {
        var dd: Any? = null
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                dd = it.getDispatcherMessage(what)
                if (dd != null) {
                    return@forEach
                }
            }
        }
        return dd as T?
    }

    /**
     * 处理基础的事件
     */
    override fun handeBaseEventer(vm: BaseViewModel) {
        //观察者
        vm.addObserver.observe(this, Observer {
            lifecycle.addObserver(it)
        })
        //清空数据
        vm.clearData.observe(this, Observer {
            clearData()
        })
        //销毁页面
        vm.finish.observe(this, Observer {
            finish()
        })
        //显示加载布局
        vm.showLoading.observe(this, Observer {
            showLoading(it)
        })
        //显示重新加载页面
        vm.showRetry.observe(this, Observer {
            showRetry(it)
        })
        //显示空页面
        vm.showEmpty.observe(this, Observer {
            showEmpty(it)
        })
        //显示内容
        vm.showContent.observe(this, Observer {
            showContent()
        })
    }

    /**
     * 清空数据  一般在列表使用  但是也可以作为其他的界面使用
     */
    open fun clearData() {

    }
}