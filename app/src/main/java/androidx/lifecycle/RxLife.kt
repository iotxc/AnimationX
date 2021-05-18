package androidx.lifecycle

import android.annotation.SuppressLint
import com.iotxc.animationx.RxLifeScope

/**
 * Author     : iot_xc
 * Date       : 2021/5/13
 * Email      : chaoxu@pateo.com.cn
 * Description: 描写描述
 */
private const val JOB_KEY = "androidx.lifecycle.ViewModelRxLifeScope.JOB_KEY"

val ViewModel.rxLifeScope: RxLifeScope
    get() {
        val scope: RxLifeScope? = this.getTag(JOB_KEY)
        if (scope != null) {
            return scope
        }
        return setTagIfAbsent(JOB_KEY, RxLifeScope())
    }

val LifecycleOwner.rxLifeScope: RxLifeScope
    get() = lifecycle.rxLifeScope

val Lifecycle.rxLifeScope: RxLifeScope
    @SuppressLint("RestrictedApi")
    get() {
        while (true) {
            val existing = mInternalScopeRef.get() as RxLifeScope?
            if (existing != null) {
                return existing
            }
            val newScope = RxLifeScope(this)
            if (mInternalScopeRef.compareAndSet(null, newScope)) {
                return newScope
            }
        }
    }