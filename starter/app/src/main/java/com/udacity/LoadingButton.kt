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
import java.lang.Math.min
import kotlin.math.absoluteValue
import kotlin.properties.Delegates
private const val START_ANGLE = 225f
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private var valueAnimator2 = ValueAnimator()
    private var radius = 0.0f                   // Radius of the circle.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 45.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }
    var value = 0f
    var sweepAngle = 0f
    var angle = -360.2f
    var width = 0f

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> {// ButtonState.Loading
                isClickable = false
                valueAnimator = ValueAnimator.ofFloat(0.0f,
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
                valueAnimator2 = ValueAnimator.ofFloat(0.0f,
                        measuredWidth.toFloat())
                        .setDuration(2000)
                        .apply {
                            addUpdateListener { valueAnimator ->
                                value = valueAnimator.animatedValue as Float
                               // sweepAngle = value *1.125f
                                repeatCount = ValueAnimator.INFINITE
                                repeatMode = ValueAnimator.REVERSE

                                invalidate()

                            }
                        }
                valueAnimator2.start()
                valueAnimator.start()
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

    }
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(ContextCompat.getColor(context,R.color.colorPrimary))
        if (buttonState == ButtonState.Loading) {
          //  canvas.drawColor(ContextCompat.getColor(context,R.color.colorPrimaryDark))
            paint.color = ContextCompat.getColor(context,R.color.colorPrimaryDark)
          canvas.drawRect(value,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
            //paint.color = ContextCompat.getColor(context, R.color.colorAccent)
            //canvas.drawArc((widthSize-radius*3).toFloat(),11.25f,(widthSize-radius).toFloat(),heightSize.toFloat()-11.25f,0f,sweepAngle,true,paint)
            if (sweepAngle<=359){
              paint.color = ContextCompat.getColor(context, R.color.colorAccent)
                canvas.drawArc((widthSize-radius*3).toFloat(),11.25f,(widthSize-radius).toFloat(),heightSize.toFloat()-11.25f,0f,sweepAngle,true,paint)
               // angle = sweepAngle
               // Log.i("angle", " sweepangle-360: "+(sweepAngle - 360).toString())
            }else{
                paint.color = ContextCompat.getColor(context, R.color.colorAccent)
                canvas.drawCircle(widthSize-2*radius,radius+11.25f,radius,paint)


              //  Log.i("angle", " angle : $angle")
                //Log.i("angle", " sweepangle-360: "+(sweepAngle - 360).toString())
                if (angle<=(sweepAngle-360)) {
                    paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    angle = (sweepAngle - 360).absoluteValue
                    //Log.i("angle", " : $angle")
                    //Log.i("angle", " sweepangle-360: "+(sweepAngle - 360).toString())
                    canvas.drawArc((widthSize - radius * 3).toFloat(), 11.25f, (widthSize - radius).toFloat(), heightSize.toFloat() - 11.25f, 0f, angle, true, paint)
                }else angle = -360f
            }

            paint.color = Color.GRAY
            canvas.drawText("LOADING...",(widthSize/2).toFloat(), (heightSize-55).toFloat(),paint)
        }
        else if(buttonState ==ButtonState.Completed) {
            paint.color = Color.WHITE
            canvas.drawText("DOWNLOAD",(widthSize/2).toFloat(), (heightSize-55).toFloat(),paint)
        }else{ Color.YELLOW}
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