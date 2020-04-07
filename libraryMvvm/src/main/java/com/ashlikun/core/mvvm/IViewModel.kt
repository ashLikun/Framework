package com.ashlikun.core.mvvm

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

/**
 * @author　　: 李坤
 * 创建时间: 2020/3/27 17:53
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：ViewModel的声明接口
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
annotation class IViewModel(val value: KClass<out BaseViewModel>)