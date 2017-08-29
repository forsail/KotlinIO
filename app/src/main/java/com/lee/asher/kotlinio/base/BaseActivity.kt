package com.lee.asher.kotlinio.base

import android.app.Activity
import android.os.Bundle
import com.lee.asher.kotlinio.extension.applyScheduleres

/**
 * Created by lihong on 2017/8/21.
 */
abstract class BaseActivity : Activity() {
    var isFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResources())
        initView()
    }

    abstract fun getLayoutResources(): Int

    abstract fun initView()
}