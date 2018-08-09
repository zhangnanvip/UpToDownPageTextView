package com.zn.flashview

import android.view.View
import android.view.ViewGroup

/**
 * @author zhangnan
 * @date 2018/8/9
 */
abstract class FlashAdapter {

    abstract fun loadView(position: Int, parent: ViewGroup): View

    abstract fun getCount(): Int

}