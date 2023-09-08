package com.dave.spellchecker.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.dave.spellchecker.R

data class ShimmerConfig(
    val shimmerColors: IntArray,
    val matrix: Matrix = Matrix(),
    val paint: Paint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    },
    var gradientShader: Shader? = null,
) {
    fun setGradientShader(width: Float) {
        gradientShader = LinearGradient(
            0f,
            0f,
            width,
            0f,
            shimmerColors,
            null,
            Shader.TileMode.CLAMP,
        )
        paint.shader = gradientShader
    }
}

data class ShimmerAnimator(
    val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(-1f, 1f),
) {
    fun setAnimationProperties() {
        valueAnimator.apply {
            duration = 2000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }
}

data class CornerRadii(
    val radius: Float,
)

class ShimmerBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var shimmerConfig: ShimmerConfig
    private lateinit var shimmerAnimator: ShimmerAnimator
    private lateinit var cornerRadii: CornerRadii
    private val rectF = RectF()

    init {
        setupAttrs(context, attrs)
        shimmerAnimator.setAnimationProperties()
//        setWillNotDraw(false)
    }

    private fun setupAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShimmerBackgroundView)
        val radius = typedArray.getDimension(R.styleable.ShimmerBackgroundView_cornerRadius, 0f)
        val shimmerColors = getShimmerColors(typedArray, context)
        shimmerConfig = ShimmerConfig(shimmerColors = shimmerColors)
        shimmerAnimator = ShimmerAnimator().apply {
            valueAnimator.addUpdateListener { animator ->
                val translationX = (width * animator.animatedValue as Float)
                shimmerConfig.matrix.setTranslate(translationX, 0f)
                shimmerConfig.gradientShader?.setLocalMatrix(shimmerConfig.matrix)
                invalidate()
            }
        }
        cornerRadii = CornerRadii(radius)
        typedArray.recycle()
    }

    private fun getShimmerColors(it: TypedArray, context: Context): IntArray {
        val shimmerColorArrayId =
            it.getResourceId(R.styleable.ShimmerBackgroundView_shimmerColors, 0)
        return if (shimmerColorArrayId != 0) {
            resources.getIntArray(shimmerColorArrayId)
        } else {
            intArrayOf(
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
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rectF, cornerRadii.radius, cornerRadii.radius, shimmerConfig.paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shimmerConfig.setGradientShader(w.toFloat())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        shimmerAnimator.valueAnimator.start()
    }

    override fun onDetachedFromWindow() {
        shimmerAnimator.valueAnimator.cancel()
        super.onDetachedFromWindow()
    }
}
