package com.dave.spellchecker.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dave.spellchecker.R

class ShimmerTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    //    private var shimmerAnimator: ValueAnimator? = null
    private val matrix = Matrix()
    private val shimmerColors = intArrayOf(
        ContextCompat.getColor(context, R.color.gold_color1),
        ContextCompat.getColor(context, R.color.gold_color2),
        ContextCompat.getColor(context, R.color.gold_color3),
        ContextCompat.getColor(context, R.color.gold_color4),
        ContextCompat.getColor(context, R.color.gold_color5),
        ContextCompat.getColor(context, R.color.gold_color4),
        ContextCompat.getColor(context, R.color.gold_color3),
        ContextCompat.getColor(context, R.color.gold_color2),
        ContextCompat.getColor(context, R.color.gold_color1),
    )

    private var gradientShader: LinearGradient? = null
    private val animator = ValueAnimator.ofFloat(-0.6f, 1.5f).apply {
        duration = 1500
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animation ->
            val animationValue = animation.animatedValue as Float
            // Update shader's offset here.
            matrix.setTranslate(width * animationValue, 0f)
            gradientShader?.setLocalMatrix(matrix)
            invalidate() // Invalidate the view to redraw the shader effect
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gradientShader = LinearGradient(
            -w.toFloat(),
            0f,
            0f,
            h.toFloat(),
            shimmerColors,
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = gradientShader
        animator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}
