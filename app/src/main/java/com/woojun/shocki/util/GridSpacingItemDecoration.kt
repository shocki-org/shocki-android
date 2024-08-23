package com.woojun.shocki.util

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class GridSpacingItemDecoration : RecyclerView.ItemDecoration() {
    private val spacing = (4f * Resources.getSystem().displayMetrics.density).toInt()
    private val additionalSpacing = (20f * Resources.getSystem().displayMetrics.density).toInt()
    private val linearStartEndLarge = (16f * Resources.getSystem().displayMetrics.density).toInt()
    private val linearStartEndSmall = (2f * Resources.getSystem().displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager

        if (position >= 0) {
            if (layoutManager is GridLayoutManager) {
                val column = position % 2

                outRect.left = if (column == 0) spacing + additionalSpacing else spacing
                outRect.right = if (column == 1) spacing + additionalSpacing else spacing

                outRect.top = if (position < 2) spacing else 0
                outRect.bottom = spacing

            } else if (layoutManager is LinearLayoutManager) {
                val isLastItem = position == state.itemCount - 1

                outRect.left = when {
                    position == 0 -> linearStartEndLarge
                    else -> linearStartEndSmall
                }

                outRect.right = when {
                    isLastItem -> linearStartEndLarge
                    else -> linearStartEndSmall
                }
            }
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }
}
