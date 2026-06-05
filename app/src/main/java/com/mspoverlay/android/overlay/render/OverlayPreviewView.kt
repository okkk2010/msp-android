package com.mspoverlay.android.overlay.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.mspoverlay.android.overlay.model.OverlayDocument

class OverlayPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val planner = OverlayRenderPlanner()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var document: OverlayDocument? = null

    fun setOverlayDocument(document: OverlayDocument?) {
        this.document = document
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val current = document ?: return
        val plan = planner.plan(current, width, height)
        plan.commands.forEach { command ->
            when (command) {
                is RectCommand -> drawRect(canvas, command)
                is CircleCommand -> drawCircle(canvas, command)
                is LineCommand -> drawLine(canvas, command)
            }
        }
    }

    private fun drawRect(canvas: Canvas, command: RectCommand) {
        val rect = RectF(command.x, command.y, command.x + command.width, command.y + command.height)
        canvas.withRotation(command.rotation, rect.centerX(), rect.centerY()) {
            command.fillColor?.let {
                paint.resetForFill(it)
                canvas.drawRoundRect(rect, command.cornerRadius, command.cornerRadius, paint)
            }
            if (command.strokeColor != null && command.strokeWidth > 0f) {
                paint.resetForStroke(command.strokeColor, command.strokeWidth)
                canvas.drawRoundRect(rect, command.cornerRadius, command.cornerRadius, paint)
            }
        }
    }

    private fun drawCircle(canvas: Canvas, command: CircleCommand) {
        val rect = RectF(command.x, command.y, command.x + command.width, command.y + command.height)
        canvas.withRotation(command.rotation, rect.centerX(), rect.centerY()) {
            command.fillColor?.let {
                paint.resetForFill(it)
                canvas.drawOval(rect, paint)
            }
            if (command.strokeColor != null && command.strokeWidth > 0f) {
                paint.resetForStroke(command.strokeColor, command.strokeWidth)
                canvas.drawOval(rect, paint)
            }
        }
    }

    private fun drawLine(canvas: Canvas, command: LineCommand) {
        val color = command.strokeColor ?: return
        if (command.strokeWidth <= 0f) {
            return
        }
        paint.resetForStroke(color, command.strokeWidth)
        paint.pathEffect = DashStyles.toPathEffect(command.dashStyle)
        canvas.drawLine(command.x1, command.y1, command.x2, command.y2, paint)
    }

    private fun Canvas.withRotation(rotation: Float, pivotX: Float, pivotY: Float, draw: () -> Unit) {
        if (rotation == 0f) {
            draw()
            return
        }
        save()
        rotate(rotation, pivotX, pivotY)
        draw()
        restore()
    }

    private fun Paint.resetForFill(colorValue: Int) {
        reset()
        isAntiAlias = true
        style = Paint.Style.FILL
        color = colorValue
    }

    private fun Paint.resetForStroke(colorValue: Int, width: Float) {
        reset()
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = width
        strokeCap = Paint.Cap.ROUND
        color = colorValue
    }
}

private object DashStyles {
    fun toPathEffect(style: String?): android.graphics.PathEffect? {
        return when (style?.lowercase()) {
            "dash" -> android.graphics.DashPathEffect(floatArrayOf(18f, 12f), 0f)
            "dot" -> android.graphics.DashPathEffect(floatArrayOf(6f, 10f), 0f)
            else -> null
        }
    }
}
