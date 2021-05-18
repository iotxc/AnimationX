package com.iotxc.animationx

import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.rxLifeScope
import com.pateo.animationx.R
import com.pateo.animationx.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.DecimalFormat
import kotlin.math.hypot

class MainActivity : AppCompatActivity() {

    companion object {
        const val ANIMATION_DURATION = 700L // 0,7s
    }

    private fun Float.dpToPx(): Float =
        this * Resources.getSystem().displayMetrics.density

    private var format = DecimalFormat("#.00")

    private var viewX = 0f
    private var viewY = 0f

    var lastClickTime = 0L
    private fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) {
        setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
                return@setOnClickListener
            }
            lastClickTime = currentTime
            action(it)
        }
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {

        }

        rxLifeScope.launch {
            AnimationX.sequentially(
                binding.cardView.scale(1f, ANIMATION_DURATION),
                binding.fab.scale(1f, ANIMATION_DURATION),
                AnimationX.together(
                    binding.fab.rotation(360f, ANIMATION_DURATION),
                    binding.text.fadeIn(ANIMATION_DURATION)
                ),
                binding.progressBar.fadeIn(ANIMATION_DURATION)
            ).collect()
        }

        binding.btnAlpha.clickNoRepeat { viewIt ->
            viewIt.run {
                launch(this.fadeOut(ANIMATION_DURATION, reverse = true).onCompletion {

                })
            }
        }

        binding.btnTranslation.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                viewIt.translation(500f, 500f, ANIMATION_DURATION, reverse = true).collect()
            }
        }

        binding.btnScale.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                viewIt.scale(0f, ANIMATION_DURATION, reverse = true).collect()
            }
        }

        binding.btnBackgroundColor.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                viewIt.backgroundColor(
                    ContextCompat.getColor(this@MainActivity, R.color.primary),
                    ContextCompat.getColor(this@MainActivity, R.color.accent),
                    ANIMATION_DURATION, reverse = true
                ).collect()
            }
        }

        binding.btnResize.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                viewIt.resize(0, 0, ANIMATION_DURATION, reverse = true).collect()
            }
        }

        binding.btnShake.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                viewIt.shake().collect()
            }
        }

        binding.btnPress.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                viewIt.press().collect()
            }
        }

        binding.btnCustomProperties.clickNoRepeat {
            rxLifeScope.launch {
                (0f to 30f).rangeAnyToCompletable(ANIMATION_DURATION, reverse = true) {
                    binding.cardCustomProperties.radius = (it as Float).dpToPx()
                }.collect()
            }
        }

        binding.tvNumber.clickNoRepeat {
            rxLifeScope.launch {
                binding.tvNumber.text = "0"
                (0f to 30f).rangeAnyToCompletable(ANIMATION_DURATION) {
                    binding.tvNumber.text = format.format(it as Float)
                }.collect()
            }
        }

        binding.textToSetText.clickNoRepeat {
            rxLifeScope.launch {
                binding.textToSetText.text("Pateo", ANIMATION_DURATION, reverse = true).collect()
            }
        }


        binding.ivRevealRect.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                viewX = event.x
                viewY = event.y
            }
            return@setOnTouchListener false
        }

        binding.ivRevealoval.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                viewX = event.x
                viewY = event.y
            }
            return@setOnTouchListener false
        }

        binding.ivRevealRect.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                binding.ivRevealRect.reveal(
                    viewX.toInt(),
                    viewY.toInt(),
                    0f,
                    hypot(
                        viewIt.width.toDouble(),
                        viewIt.height.toDouble()
                    ).toFloat(),
                    2000L
                ).collect()
            }
        }

        binding.ivRevealoval.clickNoRepeat { viewIt ->
            rxLifeScope.launch {
                binding.ivRevealoval.reveal(
                    viewIt.width / 2,
                    viewIt.height / 2,
                    viewIt.width.toFloat(),
                    0f,
                    2000L
                ).collect()
            }
        }
    }

}