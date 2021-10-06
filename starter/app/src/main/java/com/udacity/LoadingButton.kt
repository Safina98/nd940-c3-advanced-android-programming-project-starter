package com.udacity

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Color.CYAN
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import java.lang.Math.min
import kotlin.math.absoluteValue
import kotlin.properties.Delegates
private const val START_ANGLE = 225f
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var loadingButtonCompleteColor = 0
    private var loadingButtonLoadingColor = 0
    //private var textPos = 0
    private var textDownload=""
    private var textDownloading =""
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimatorCircle = ValueAnimator()
    private var valueAnimatorRect = ValueAnimator()
    private var radius = 0.0f                   // Radius of the circle.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 45.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }
    private var value = 0f
    private var sweepAngle = 0f
    private var angle = -360.2f

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> {
                isClickable = false
                valueAnimatorCircle = ValueAnimator.ofFloat(0.0f,
                        measuredWidth.toFloat())
                        .setDuration(4000)
                        .apply {
                            addUpdateListener { valueAnimator ->
                                var value = valueAnimator.animatedValue as Float
                                sweepAngle = value *1.125f
                                repeatCount = ValueAnimator.INFINITE
                                repeatMode = ValueAnimator.RESTART
                                interpolator = AccelerateInterpolator(1f)
                                invalidate()
                            }
                        }
                valueAnimatorRect = ValueAnimator.ofFloat(0.0f,
                        measuredWidth.toFloat())
                        .setDuration(2000)
                        .apply {
                            addUpdateListener { valueAnimator ->
                                value = valueAnimator.animatedValue as Float
                                repeatCount = ValueAnimator.INFINITE
                                repeatMode = ValueAnimator.REVERSE
                                invalidate()
                            }
                        }
                valueAnimatorRect.start()
                valueAnimatorCircle.start()
            }
            ButtonState.Completed ->{
                ButtonState.Completed
                invalidate()
                isClickable = true
            }
        }
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingButtonCompleteColor = getColor(R.styleable.LoadingButton_bgColor1, 0)
            loadingButtonLoadingColor = getColor(R.styleable.LoadingButton_bgColor2, 0)
            textDownload = getText(R.styleable.LoadingButton_textDownload).toString()
            textDownloading = getText(R.styleable.LoadingButton_textDownloading).toString()

//            textDownloading = getInteger(R.styleable.LoadingButton_textDownloading, 0)
        }

    }
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawColor(ContextCompat.getColor(context,R.color.colorPrimary))
        canvas.drawColor(loadingButtonCompleteColor)
        when (buttonState) {
            ButtonState.Loading -> {

                paint.color = loadingButtonLoadingColor
                canvas.drawRect(value,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
                if (sweepAngle<=359){
                    paint.color = ContextCompat.getColor(context, R.color.colorAccent)
                    canvas.drawArc((widthSize-radius*3).toFloat(),11.25f,(widthSize-radius).toFloat(),heightSize.toFloat()-11.25f,0f,sweepAngle,true,paint)
                }else{
                    paint.color = ContextCompat.getColor(context, R.color.colorAccent)
                    canvas.drawCircle(widthSize-2*radius,radius+11.25f,radius,paint)
                    if (angle<=(sweepAngle-360)) {
                        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                        angle = (sweepAngle - 360).absoluteValue
                        canvas.drawArc((widthSize - radius * 3), 11.25f, (widthSize - radius), heightSize.toFloat() - 11.25f, 0f, angle, true, paint)
                    }else angle = -360f
                }
                paint.color = Color.GRAY
                canvas.drawText(textDownloading,(widthSize/2).toFloat(), (heightSize-55).toFloat(),paint)
            }
            ButtonState.Completed -> {
                paint.color = Color.WHITE
                canvas.drawText(textDownload,(widthSize/2).toFloat(), (heightSize-55).toFloat(),paint)
            }
            else -> { Color.YELLOW}
        }
        paint.color = context.getColor(R.color.colorPrimary)
    }
       override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


}