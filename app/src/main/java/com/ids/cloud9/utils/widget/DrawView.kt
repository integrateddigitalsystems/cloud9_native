package com.ids.cloud9.utils.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.widget.MyPath
import com.ids.cloud9.widget.PaintOptions
import java.util.*


class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPaths = LinkedHashMap<MyPath, PaintOptions>()

    private var mLastPaths = LinkedHashMap<MyPath, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<MyPath, PaintOptions>()
    var bmp: Bitmap? = null
    private var mPaint = Paint()
    private var mPath = MyPath()
    private var mPaintOptions = PaintOptions()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mIsSaving = false
    private var mIsStrokeWidthBarEnabled = false

    var isEraserOn = false
        private set

    init {
        mPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
            //added
            isAntiAlias=true
            isFilterBitmap=true
            isDither=true

        }

      //  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
    }

    fun setBitmap(bitmap:Bitmap){

        clearCanvas()
        bmp = bitmap
        mPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
        }
    }

    fun undo() {
        if (mPaths.isEmpty() && mLastPaths.isNotEmpty()) {
            mPaths = mLastPaths.clone() as LinkedHashMap<MyPath, PaintOptions>
            mLastPaths.clear()
            invalidate()
            return
        }
        if (mPaths.isEmpty()) {
            return
        }
        val lastPath = mPaths.values.lastOrNull()
        val lastKey = mPaths.keys.lastOrNull()

        mPaths.remove(lastKey)
        if (lastPath != null && lastKey != null) {
            mUndonePaths[lastKey] = lastPath
        }
        invalidate()
    }

    fun redo() {
        if (mUndonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = mUndonePaths.keys.last()
        addPath(lastKey, mUndonePaths.values.last())
        mUndonePaths.remove(lastKey)
        invalidate()
    }

    fun setColor(newColor: Int) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha*255)/100
        mPaintOptions.alpha = alpha
        setColor(mPaintOptions.color)
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        return bitmap
    }

    fun addPath(path: MyPath, options: PaintOptions) {
        mPaths[path] = options
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bmp != null) {
            var centreX = (canvas.width  - bmp!!.width) /2

           var  centreY = (canvas.height - bmp!!.height) /2

/*            val frameToDraw = Rect(0, 0, width, height)
            val whereToDraw = RectF(0f, 0f, width.toFloat(), height.toFloat())*/
            Log.wtf("bitmap_width",bmp!!.width.toString())
            Log.wtf("bitmap_height",bmp!!.width.toString())

           // var scaledbitmap=scale(bmp!!,800,400)
           // var scaledbitmap= Bitmap.createScaledBitmap(bmp!!,(bmp!!.getWidth()*0.8).toInt(), (bmp!!.getHeight()*0.8).toInt(), true);
           // var scaledbitmap= Bitmap.createScaledBitmap(bmp!!,(canvas!!.getWidth()*0.5).toInt(), (canvas!!.getHeight()*0.6).toInt(), true);

          /*  val out =
                Bitmap.createScaledBitmap(bmp!!, thumbWidth as Int, thumbHeight as Int, false)
*/
            canvas.drawBitmap(
                bmp!!,
                Rect(0, 0, bmp!!.width, bmp!!.height),
                RectF(0f, 0f, width.toFloat(), height.toFloat()),
                mPaint
            )

          //  canvas.drawBitmap(bmp!!, centreX.toFloat(), centreY.toFloat(), mPaint)
        }
        for ((key, value) in mPaths) {
            changePaint(value)
            canvas.drawPath(key, mPaint)
        }

        changePaint(mPaintOptions)
        canvas.drawPath(mPath, mPaint)
    }

    private fun changePaint(paintOptions: PaintOptions) {
        mPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        mPaint.strokeWidth = paintOptions.strokeWidth
    }


    private fun scale(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        // Determine the constrained dimension, which determines both dimensions.
        val width: Int
        val height: Int
        val widthRatio = bitmap.width.toFloat() / maxWidth
        val heightRatio = bitmap.height.toFloat() / maxHeight
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth
            height = (width.toString().toFloat() / bitmap.width * bitmap.height).toInt()
        } else {
            height = maxHeight
            width = (height.toString().toFloat() / bitmap.height * bitmap.width).toInt()
        }
        val scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val ratioX = width.toString().toFloat() / bitmap.width
        val ratioY = height.toString().toFloat() / bitmap.height
        val middleX = width / 2.0f
        val middleY = height / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap.width / 2,
            middleY - bitmap.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaledBitmap
    }

    fun clearCanvas() {
        mLastPaths = mPaths.clone() as LinkedHashMap<MyPath, PaintOptions>
        mPath.reset()
        mPaths.clear()
        invalidate()
    }

    private fun actionDown(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    private fun actionMove(x: Float, y: Float) {
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        mPath.lineTo(mCurX, mCurY)

        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY)
        }

        mPaths[mPath] = mPaintOptions
        mPath = MyPath()
        mPaintOptions = PaintOptions(mPaintOptions.color, mPaintOptions.strokeWidth, mPaintOptions.alpha, mPaintOptions.isEraserOn)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                MyApplication.isSignatureEmpty=false
                mStartX = x
                mStartY = y
                actionDown(x, y)
                mUndonePaths.clear()
            }
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }

        invalidate()
        return true
    }

    fun toggleEraser() {
        isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()
    }

}