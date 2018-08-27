package com.zn.flashview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView

/**
 * @author zhangnan
 * @date 2018/8/2
 */
class FlashView : LinearLayout {

    var onItemClickListener: OnItemClickListener? = null
    var intervalTime: Long = 3000
    var durationTime: Long = 3000
    var interpolator: Interpolator = LinearInterpolator()
    val data: MutableList<String> = mutableListOf()

    private var animationSet: AnimatorSet? = null
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private var textLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    private var currentPageIndex = 0
    private var nextPageIndex = currentPageIndex + 1
    private var flag: Boolean = true
    private var isInitialized = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        animationSet?.cancel()
        textView1 = TextView(context)
        textView2 = TextView(context)
        currentPageIndex = 0
        nextPageIndex = currentPageIndex + 1
        flag = true
        isInitialized = false
        textLayoutParams.gravity = Gravity.CENTER

        removeAllViews()
        when (data.size) {
            0 -> {
            }
            1 -> {
                textView1.text = data[currentPageIndex]
                textView1.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                textView1.ellipsize = TextUtils.TruncateAt.END
                addView(textView1)
            }
            else -> {
                textView1.text = data[nextPageIndex]
                textView2.text = data[currentPageIndex]
                textView1.setOnClickListener { onItemClickListener?.onItemClick(nextPageIndex) }
                textView2.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                textView1.ellipsize = TextUtils.TruncateAt.END
                textView2.ellipsize = TextUtils.TruncateAt.END
                addView(textView1)
                addView(textView2)
            }
        }
    }

    private fun loop(translationY: Float) {
        val animator1 = ObjectAnimator.ofFloat(textView1, "translationY", translationY)
        val animator2 = ObjectAnimator.ofFloat(textView2, "translationY", translationY)
        if (animationSet == null) {
            animationSet = AnimatorSet()
            animationSet?.addListener(LoopAnimator(translationY))
        }
        animationSet?.interpolator = interpolator
        animationSet?.duration = durationTime
        animationSet?.playTogether(animator1, animator2)
        animationSet?.startDelay = intervalTime
        animationSet?.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (data.size > 1 && !isInitialized) {
            val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
            val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
            textLayoutParams.width = parentWidth
            textLayoutParams.height = parentHeight
            textView1.layoutParams = textLayoutParams
            textView2.layoutParams = textLayoutParams
            textView1.y = -parentHeight.toFloat() / 2
            textView2.y = -parentHeight.toFloat() / 2
            loop(parentHeight.toFloat() / 2)
            isInitialized = true
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun initData(data: List<String>) {
        updateData(data)
    }

    fun updateData(data: List<String>) {
        this.data.clear()
        this.data.addAll(data)
        initView()
    }

    fun addData(data: List<String>) {
        this.data.addAll(data)
        initView()
    }

    fun clearData() {
        this.data.clear()
        initView()
    }

    fun setTextSize(size: Float): FlashView {
        textView1.textSize = size
        textView2.textSize = size
        return this
    }

    fun setTextSize(unit: Int, size: Float): FlashView {
        textView1.setTextSize(unit, size)
        textView2.setTextSize(unit, size)
        return this
    }

    fun setTextColor(color: Int): FlashView {
        textView1.setTextColor(color)
        textView2.setTextColor(color)
        return this
    }

    fun setSingleLine(): FlashView {
        textView1.setSingleLine()
        textView2.setSingleLine()
        textView1.maxLines = 1
        textView2.maxLines = 1
        return this
    }

    fun setTextPadding(left: Int, top: Int, right: Int, bottom: Int): FlashView {
        textView1.setPadding(left, top, right, bottom)
        textView2.setPadding(left, top, right, bottom)
        return this
    }

    fun setTextGravity(gravity: Int): FlashView {
        textView1.gravity = gravity
        textView2.gravity = gravity
        return this
    }

    interface OnItemClickListener {

        fun onItemClick(position: Int)

    }

    inner class LoopAnimator(private val translationY: Float) : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (currentPageIndex < data.size - 1) {
                currentPageIndex++
            } else {
                currentPageIndex = 0
            }
            if (nextPageIndex < data.size - 1) {
                nextPageIndex++
            } else {
                nextPageIndex = 0
            }
            flag = if (flag) {
                removeAllViews()
                addView(textView2)
                addView(textView1)
                textView1.y = -translationY * 2
                textView2.y = -translationY / 2
                textView2.text = data[nextPageIndex]
                textView1.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                textView2.setOnClickListener { onItemClickListener?.onItemClick(nextPageIndex) }
                false
            } else {
                removeAllViews()
                addView(textView1)
                addView(textView2)
                textView1.y = -translationY / 2
                textView2.y = -translationY * 2
                textView1.text = data[nextPageIndex]
                textView2.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                textView1.setOnClickListener { onItemClickListener?.onItemClick(nextPageIndex) }
                true
            }
            loop(translationY)
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }
}