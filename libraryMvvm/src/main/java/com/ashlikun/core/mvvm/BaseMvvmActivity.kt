package com.ashlikun.core.mvvm

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ashlikun.core.activity.BaseActivity


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
    val viewModel: VM
        get() = viewModelProvider[initViewModel()]

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.dataInit
        super.onCreate(savedInstanceState)
    }

    override fun initLoadSwitch() {
        super.initLoadSwitch()
        viewModel.loadSwitchService = switchService
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                it.loadSwitchService = switchService
            }
        }
    }

    override fun parseIntent(intent: Intent?) {
        super.parseIntent(intent)
        if (intent != null) {
            viewModel.parseIntent(intent!!)
            viewModelProvider.forEach<ViewModel> {
                if (it is BaseViewModel) {
                    it.parseIntent(intent!!)
                }
            }
        }
    }

    protected open fun initViewModel(): Class<VM> {
        //获取注解
        var modelClass: Class<VM>? = ViewModelFactoryImp.getViewModelAnnotation(javaClass)
                ?: ViewModelFactoryImp.getViewModelParameterizedType(javaClass)
                ?: throw RuntimeException("ViewModel创建失败!检查是否声明了@ViewModel(XXX.class)注解  或者 从写initViewModel方法 或者当前View的泛型没用ViewModel")
        return modelClass!!
    }

    override fun onDispatcherMessage(what: Int, bundle: Bundle?) {
        super.onDispatcherMessage(what, bundle)
        viewModel.onDispatcherMessage(what, bundle)
        viewModelProvider.forEach<ViewModel> {
            if (it is BaseViewModel) {
                it.onDispatcherMessage(what, bundle)
            }
        }
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