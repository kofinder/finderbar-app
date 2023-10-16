package com.finderbar.jovian.utilities
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.finderbar.jovian.R
import com.finderbar.jovian.models.BadgeCounts

/**
 * Created by thein on 12/18/18.
 */

class BadgeView : View {

    private val iconHeight by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, context.resources.displayMetrics) }
    private val iconRadius by lazy { iconHeight / 2f }
    private val startEndPadding by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics) }
    private val iconLabelPadding by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics) }
    private val badgePadding by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, context.resources.displayMetrics) }

    // colors
    private val iconColors by lazy {
        listOf(
                ContextCompat.getColor(context, R.color.goldBadgeColor),
                ContextCompat.getColor(context, R.color.silverBadgeColor),
                ContextCompat.getColor(context, R.color.bronzeBadgeColor)
        )
    }

    // paint
    private val iconPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val textPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, context.resources.displayMetrics)
            typeface = Typeface.DEFAULT
        }
    }
    private val textHeight = textPaint.fontMetrics.bottom - textPaint.fontMetrics.top

    // extras
    private val labelHelperRect by lazy { Rect() }

    // badge data
    var badgeCounts: BadgeCounts? = null
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        if (isInEditMode) {
            badgeCounts = BadgeCounts(80, 3, 4)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var idealWidth = paddingLeft + paddingRight + suggestedMinimumWidth + 0f
        val idealHeight = paddingTop + paddingBottom + suggestedMinimumHeight + textHeight

        badgeCounts?.let {
            var addPadding = false
            var badgeCount = 0

            // add space for each badge
            listOf(it.gold, it.silver, it.bronze).forEach {
                if (it!! > 0) {
                    idealWidth += iconHeight + iconLabelPadding + textPaint.measureText(it.toString())
                    addPadding = true
                    badgeCount += 1
                }
            }

            // add padding between badges
            if (badgeCount >= 2) {
                idealWidth += badgePadding * (badgeCount - 1)
            }

            // add start and end padding
            if (addPadding) {
                idealWidth += startEndPadding * 2
            }
        }

        setMeasuredDimension(
                View.resolveSize(idealWidth.toInt(), widthMeasureSpec),
                View.resolveSize(idealHeight.toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        badgeCounts?.let {
            val canvasHeight = canvas.height
            val centerY = canvasHeight / 2f
            var posX = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paddingStart + startEndPadding
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN_MR1")
            }

            listOf(it.gold, it.silver, it.bronze).forEachIndexed { index, amount ->
                if (amount!! > 0) {
                    // draw icon
                    posX += iconRadius
                    iconPaint.color = iconColors[index]
                    canvas.drawCircle(posX, centerY, iconRadius, iconPaint)

                    // draw label
                    val amountString = amount.toString()
                    posX += iconRadius + iconLabelPadding
                    textPaint.getTextBounds(amountString, 0, amountString.length, labelHelperRect)
                    canvas.drawText(amountString, posX, canvasHeight - paddingBottom - textPaint.fontMetrics.descent, textPaint)
                    posX += textPaint.measureText(amountString) + badgePadding
                }
            }
        }
    }
}
