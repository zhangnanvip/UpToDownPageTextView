package com.zn.flashview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
 * @author zhangnan
 * @date 2018/8/8
 */
/**
 * @author zhangnan
 * @date 2018/8/2
 */
class FlashLayout : LinearLayout {

    var onItemClickListener: OnItemClickListener? = null
    var intervalTime: Long = 3000
    var durationTime: Long = 3000
    var interpolator: Interpolator = LinearInterpolator()
    var adapter: FlashAdapter? = null
        set(value) {
            field = value
            initView()
        }

    private var animationSet: AnimatorSet? = null
    private val container1 = FrameLayout(context)
    private val container2 = FrameLayout(context)
    private val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    private var currentPageIndex = 0
    private var nextPageIndex = currentPageIndex + 1
    private var flag: Boolean = true
    private var isInitialized = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    private fun initView() {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        layoutParams.gravity = Gravity.CENTER

        removeAllViews()
        when (adapter?.getCount()) {
            0 -> animationSet?.cancel()
            1 -> {
                container1.removeAllViews()
                container1.addView(adapter?.loadView(currentPageIndex, container1))
                container1.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                addView(container1)
            }
            else -> {
                container1.removeAllViews()
                container2.removeAllViews()
                container1.addView(adapter?.loadView(nextPageIndex, container1))
                container2.addView(adapter?.loadView(currentPageIndex, container2))
                container1.setOnClickListener { onItemClickListener?.onItemClick(nextPageIndex) }
                container2.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                addView(container1)
                addView(container2)
            }
        }
    }

    private fun loop(translationY: Float) {
        val animator1 = ObjectAnimator.ofFloat(container1, "translationY", translationY)
        val animator2 = ObjectAnimator.ofFloat(container2, "translationY", translationY)
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
        if ((adapter?.getCount() ?: 0) > 1 && !isInitialized) {
            val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
            val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
            layoutParams.width = parentWidth
            layoutParams.height = parentHeight
            container1.layoutParams = layoutParams
            container2.layoutParams = layoutParams
            container1.y = -parentHeight.toFloat() / 2
            container2.y = -parentHeight.toFloat() / 2
            loop(parentHeight.toFloat() / 2)
            isInitialized = true
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    interface OnItemClickListener {

        fun onItemClick(position: Int)

    }

    inner class LoopAnimator(private val translationY: Float) : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (currentPageIndex < (adapter?.getCount() ?: 0) - 1) {
                currentPageIndex++
            } else {
                currentPageIndex = 0
            }
            if (nextPageIndex < (adapter?.getCount() ?: 0) - 1) {
                nextPageIndex++
            } else {
                nextPageIndex = 0
            }
            flag = if (flag) {
                removeAllViews()
                addView(container2)
                addView(container1)
                container1.y = -translationY * 2
                container2.y = -translationY / 2
                container2.removeAllViews()
                container2.addView(adapter?.loadView(nextPageIndex, container2))
                container1.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                container2.setOnClickListener { onItemClickListener?.onItemClick(nextPageIndex) }
                false
            } else {
                removeAllViews()
                addView(container1)
                addView(container2)
                container1.y = -translationY / 2
                container2.y = -translationY * 2
                container1.removeAllViews()
                container1.addView(adapter?.loadView(nextPageIndex, container1))
                container2.setOnClickListener { onItemClickListener?.onItemClick(currentPageIndex) }
                container1.setOnClickListener { onItemClickListener?.onItemClick(nextPageIndex) }
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