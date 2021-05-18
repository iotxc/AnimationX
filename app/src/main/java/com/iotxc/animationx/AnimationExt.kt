package com.iotxc.animationx

import android.animation.*
import android.annotation.TargetApi
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewPropertyAnimator
import android.view.animation.CycleInterpolator
import android.view.animation.Interpolator
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Author     : iot_xc
 * Date       : 2021/5/17
 * Email      : chaoxu@pateo.com.cn
 * Description: 描写描述
 */

const val TAG = "AnmationEXT"

private fun Int.dpToPx(): Int =
    (this * Resources.getSystem().displayMetrics.density).toInt()

private fun Float.dpToPx(): Float =
    this * Resources.getSystem().displayMetrics.density

private fun Int.pxToDp(): Int =
    (this / Resources.getSystem().displayMetrics.density).toInt()

private fun ViewPropertyAnimator.animate(animationEnd: (() -> Unit)? = null) {
    withEndAction { animationEnd?.invoke() }.start()
}

/**
 * 倒转动画
 */
private fun Flow<Any>.reverse(
    reverse: Boolean,
    reverseCompletable: () -> Flow<Any>
): Flow<Any> {
    return run {
        if (reverse) this.onCompletion {
            reverseCompletable().collect()
        } else
            return@run this
    }
}

fun View.launch(action: Flow<Any>) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate +
            CoroutineExceptionHandler { _, throwable ->
                Log.d(
                    TAG,
                    throwable.toString()
                )
            })

    this.requestManager.setCurrentRequestJob(scope.launch {
        action.collect()
    })
}

fun View.animate(
    alpha: Float? = null,
    translationX: Float? = null,
    translationY: Float? = null,
    scaleX: Float? = null,
    scaleY: Float? = null,
    rotation: Float? = null,
    rotationX: Float? = null,
    rotationY: Float? = null,
    x: Float? = null,
    y: Float? = null,
    z: Float? = null,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null
): Flow<Any> =
    flow {
        emit(animate().apply {
            alpha?.also { alpha(it) }
            translationX?.also { translationX(it.dpToPx()) }
            translationY?.also { translationY(it.dpToPx()) }
            scaleX?.also { scaleX(it) }
            scaleY?.also { scaleY(it) }
            rotation?.also { rotation(it) }
            rotationX?.also { rotationX(it) }
            rotationY?.also { rotationY(it) }
            x?.also { x(it) }
            y?.also { x(it) }
            z?.also { x(it) }
            duration?.also { this.duration = it }
            interpolator?.also { this.interpolator = it }
            startDelay?.also { this.startDelay = it }
        }.animate())
        delay(duration ?: 0)
    }

private fun ValueAnimator.start(
    duration: Long? = null,
    interpolator: Interpolator? = null,
    animationEnd: (() -> Unit)? = null,
    action: (Any) -> Unit
) {
    apply {
        duration?.also { this.duration = it }
        interpolator?.also { this.interpolator = interpolator }
        addUpdateListener { action(it.animatedValue) }
        animationEnd?.also {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    it()
                }
            })
        }
    }.start()
}

fun ValueAnimator.start(
    duration: Long? = null,
    interpolator: Interpolator? = null,
    action: (Any) -> Unit,
    onComplete: (() -> Unit)? = null
): Flow<Any> =
    flowOf(
        start(
            duration,
            interpolator,
            animationEnd = { onComplete?.invoke() },
            action = action
        )
    )

fun Pair<Any, Any>.rangeAnyToCompletable(
    duration: Long? = null,
    evaluator: TypeEvaluator<*>? = null,
    reverse: Boolean = false,
    onComplete: (() -> Unit)? = null,
    action: (Any) -> Unit
): Flow<Any> =
    flow {
        emit(
            ValueAnimator.ofObject(evaluator, first, second)
                .start(duration,
                    animationEnd = { onComplete?.invoke() },
                    action = { value -> (value as? Any)?.also { action(it) } })
        )
        delay(duration ?: 0)
    }.reverse(reverse) {
        (second to first).rangeAnyToCompletable(
            duration,
            evaluator,
            action = action,
            onComplete = onComplete
        )
    }

fun View.alpha(
    alpha: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flow {
        emit(this@alpha.alpha)
    }.flatMapConcat { defaultAlpha ->
        animate(
            alpha = alpha,
            duration = duration,
            interpolator = interpolator,
            startDelay = startDelay
        ).reverse(reverse = reverse) {
            alpha(defaultAlpha, duration, interpolator)
        }
    }

fun View.fadeIn(
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
) = alpha(1f, duration, interpolator, startDelay, reverse)

fun View.fadeOut(
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
) = alpha(0f, duration, interpolator, startDelay, reverse)


fun View.translation(
    translationX: Float,
    translationY: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.translationX to this.translationY)
        .flatMapConcat { (defaultTranslationX, defaultTranslationY) ->
            animate(
                translationX = translationX,
                translationY = translationY,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                translation(
                    defaultTranslationX,
                    defaultTranslationY,
                    duration,
                    interpolator
                )
            }
        }

fun View.translationX(
    translationX: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.translationX)
        .flatMapConcat { defaultTranslationX ->
            animate(
                translationX = translationX,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                translationX(defaultTranslationX, duration, interpolator)
            }
        }

fun View.translationY(
    translationY: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.translationY)
        .flatMapConcat { defaultTranslationY ->
            animate(
                translationY = translationY,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                translationY(defaultTranslationY, duration, interpolator)
            }
        }

fun View.scale(
    scale: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.scaleX to this.scaleY)
        .flatMapConcat { (defaultScaleX, defaultScaleY) ->
            animate(
                scaleX = scale,
                scaleY = scale,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                animate(
                    scaleX = defaultScaleX,
                    scaleY = defaultScaleY,
                    duration = duration,
                    interpolator = interpolator
                )
            }
        }

fun View.scaleX(
    scaleX: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.scaleX)
        .flatMapConcat { defaultScaleX ->
            animate(
                scaleX = scaleX,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                scaleX(defaultScaleX, duration, interpolator)
            }
        }

fun View.scaleY(
    scaleY: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.scaleY)
        .flatMapConcat { defaultScaleY ->
            animate(
                scaleY = scaleY,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                scaleY(defaultScaleY, duration, interpolator)
            }
        }

fun View.rotation(
    rotation: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.rotation)
        .flatMapConcat { defaultRotation ->
            animate(
                rotation = rotation,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                rotation(defaultRotation, duration, interpolator)
            }
        }

fun View.rotationX(
    rotationX: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.rotationX)
        .flatMapConcat { defaultRotationX ->
            animate(
                rotationX = rotationX,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                rotationX(defaultRotationX, duration, interpolator)
            }
        }

fun View.rotationY(
    rotationY: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.rotationY)
        .flatMapConcat { defaultRotationY ->
            animate(
                rotationY = rotationY,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                rotationY(defaultRotationY, duration, interpolator)
            }
        }

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun View.xyz(
    x: Float? = null,
    y: Float? = null,
    z: Float? = null,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(Triple(this.x, this.y, this.z))
        .flatMapConcat { (defaultX, defaultY, defaultZ) ->
            animate(
                x = x,
                y = y,
                z = z,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                xyz(defaultX, defaultY, defaultZ, duration, interpolator)
            }
        }

fun View.x(
    x: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.x)
        .flatMapConcat { defaultX ->
            animate(
                x = x,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                x(defaultX, duration, interpolator)
            }
        }

fun View.y(
    y: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.y)
        .flatMapConcat { defaultY ->
            animate(
                y = y,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                y(defaultY, duration, interpolator)
            }
        }

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun View.z(
    z: Float,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.z)
        .flatMapConcat { defaultZ ->
            animate(
                z = z,
                duration = duration,
                interpolator = interpolator,
                startDelay = startDelay
            ).reverse(reverse) {
                z(defaultZ, duration, interpolator)
            }
        }

private fun View.widthToCompletable(
    width: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null
): Flow<Any> =
    flow {
        emit(ValueAnimator.ofInt(this@widthToCompletable.width, width.dpToPx())
            .start(duration, interpolator) { value ->
                (value as? Int)?.also { layoutParams.width = it }
                requestLayout()
            })
        delay(duration ?: 0)
    }

private fun View.heightToCompletable(
    height: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null
): Flow<Any> =
    flow {
        emit(ValueAnimator.ofInt(this@heightToCompletable.height, height.dpToPx())
            .start(duration, interpolator) { value ->
                (value as? Int)?.also { layoutParams.height = it }
                requestLayout()
            })
        delay(duration ?: 0)
    }

fun View.resize(
    width: Int, height: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.width.pxToDp() to this.height.pxToDp())
        .flatMapConcat { (defaultWidth, defaultHeight) ->
            width(width, duration, interpolator)
                .flatMapMerge { height(height, duration, interpolator) }
                .reverse(reverse) {
                    resize(defaultWidth, defaultHeight, duration, interpolator)
                }
        }

fun View.width(
    width: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.width)
        .flatMapConcat { defaultWidth ->
            widthToCompletable(width, duration, interpolator)
                .reverse(reverse) {
                    widthToCompletable(defaultWidth, duration, interpolator)
                }
        }

fun View.height(
    height: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null,
    reverse: Boolean = false
): Flow<Any> =
    flowOf(this.height)
        .flatMapConcat { defaultHeight ->
            heightToCompletable(height, duration, interpolator)
                .reverse(reverse) {
                    heightToCompletable(defaultHeight, duration, interpolator)
                }
        }

private fun View.backgroundColorToCompletable(
    colorFrom: Int,
    colorTo: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null
): Flow<Any> =
    flow {
        emit(ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            .start(duration, interpolator) { value ->
                (value as? Int)?.also { setBackgroundColor(it) }
            })
        delay(duration ?: 0)
    }

fun View.backgroundColor(
    colorFrom: Int,
    colorTo: Int,
    duration: Long? = null,
    interpolator: Interpolator? = null,
    reverse: Boolean = false
): Flow<Any> =
    backgroundColorToCompletable(colorFrom, colorTo, duration, interpolator)
        .reverse(reverse) {
            backgroundColorToCompletable(colorTo, colorFrom, duration, interpolator)
        }

fun View.shake(
    duration: Long = 300,
    nbShake: Float = 2f,
    shakeTranslation: Float = 5f
): Flow<Any> =
    flow {
        emit(animate().apply {
            this.duration = duration
            interpolator = CycleInterpolator(nbShake)
            translationX(-shakeTranslation.dpToPx())
            translationX(shakeTranslation.dpToPx())
        }.animate { })
        delay(duration)
    }

fun View.press(
    depth: Float = 0.95f,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null
): Flow<Any> =
    scale(depth, (duration ?: 300) / 2, interpolator, startDelay, reverse = true)


fun TextView.text(
    text: String,
    duration: Long = 300L,
    interpolator: TimeInterpolator? = null,
    startDelay: Long? = null,
    reverse: Boolean = false
): Flow<Any> =
    flow {
        fadeOut(duration / 2, interpolator, startDelay)
            .collect { delay(duration / 2) }
        emit(this@text.text.toString())
    }.flatMapConcat { defaultText ->
        this@text.text = text
        fadeIn(duration / 2, interpolator, startDelay = 300L)
            .onCompletion {
                delay(duration / 2)
            }.reverse(reverse) {
                text(defaultText, duration, interpolator)
            }
    }

fun View.reveal(
    centerX: Int = 0,
    centerY: Int = 0,
    startRadius: Float = 0f,
    endRadius: Float = 0f,
    duration: Long = 300L,
    startDelay: Long? = null
): Flow<Any> =
    flowOf(
        ViewAnimationUtils.createCircularReveal(
            this@reveal, centerX, centerY, startRadius, endRadius
        )
            .apply {
                this.duration = duration
                startDelay?.also { this.startDelay = it }
            }.start()
    )

