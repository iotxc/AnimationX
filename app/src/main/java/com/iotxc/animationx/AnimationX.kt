package com.iotxc.animationx

import android.view.View
import kotlinx.coroutines.flow.*


/**
 * Author     : iot_xc
 * Date       : 2021/5/18
 * Email      : chaoxu@pateo.com.cn
 * Description: 描写描述
 */
object AnimationX {

    fun from(view: View) = flow { emit(view) }

   fun together(vararg action: Flow<Any>) = flowOf(*action)
       .flattenMerge()

    fun sequentially(vararg action: Flow<Any>) =
        flowOf(*action)
            .flattenConcat()
}