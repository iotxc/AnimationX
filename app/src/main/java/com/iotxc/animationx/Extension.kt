package com.iotxc.animationx

import android.view.View
import com.pateo.animationx.R

/**
 * Author     : iot_xc
 * Date       : 2021/5/14
 * Email      : chaoxu@pateo.com.cn
 * Description: 描写描述
 */

val View.requestManager: ViewTargetRequestManager
    get() {
        var manager = getTag(R.id.view_request_manager) as? ViewTargetRequestManager
        if (manager == null) {
            manager = synchronized(this) {
                // Check again in case coil_request_manager was just set.
                (getTag(R.id.view_request_manager) as? ViewTargetRequestManager)
                    ?.let { return@synchronized it }

                ViewTargetRequestManager().apply {
                    addOnAttachStateChangeListener(this)
                    setTag(R.id.view_request_manager, this)
                }
            }
        }
        return manager
    }
