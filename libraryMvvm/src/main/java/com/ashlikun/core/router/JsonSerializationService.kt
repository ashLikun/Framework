package com.ashlikun.core.router

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间: 2021/6/23　10:32
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Router 传递数据为对象时候的Json序列化
 */
@Route(path = "/service/json")
class JsonSerializationService : SerializationService {
    override fun init(context: Context) {

    }

    override fun <T : Any?> json2Object(input: String, clazz: Class<T>): T {
        return Gson().fromJson(input, clazz)
    }

    override fun object2Json(instance: Any?): String {
        if (instance == null) {
            return ""
        }
        return Gson().toJson(instance)
    }

    override fun <T : Any?> parseObject(input: String, clazz: Type?): T {
        return Gson().fromJson(input, clazz)
    }
}