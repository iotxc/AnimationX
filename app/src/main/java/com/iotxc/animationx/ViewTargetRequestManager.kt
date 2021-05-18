package com.iotxc.animationx

import android.util.Log
import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

/**
 * Author     : iot_xc
 * Date       : 2021/5/14
 * Email      : chaoxu@pateo.com.cn
 * Description: 描写描述
 */
class ViewTargetRequestManager : View.OnAttachStateChangeListener,
    CompletionHandler{
    @Volatile
    var currentRequestJob: Job? = null
        private set

    /** Set the current [job] attached to this view and assign it an ID. */
    @MainThread
    fun setCurrentRequestJob(job: Job) {
        currentRequestJob = job
    }

    /** Detach the current request from this view. */
    @AnyThread
    fun clearCurrentRequest() {
        currentRequestJob?.cancelChildren()
        currentRequestJob?.cancel()
        currentRequestJob = null
    }

    @MainThread
    override fun onViewAttachedToWindow(v: View) {
        Log.d("coroutineScope", "onViewAttachedToWindow")
    }

    @MainThread
    override fun onViewDetachedFromWindow(v: View) {
        Log.d("coroutineScope", "onViewDetachedFromWindow ${currentRequestJob==null}")
        clearCurrentRequest()
    }

    override fun invoke(cause: Throwable?) {
        Log.d("coroutineScope", "onViewDetachedFromWindow ${currentRequestJob==null}")
        clearCurrentRequest()
    }
}