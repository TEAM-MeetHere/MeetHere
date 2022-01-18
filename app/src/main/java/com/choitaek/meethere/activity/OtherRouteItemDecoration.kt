package com.choitaek.meethere.activity

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class OtherRouteItemDecoration(private val divHeight : Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = divHeight

        view.setBackgroundColor(Color.parseColor("#FFECE9E9"))
        ViewCompat.setElevation(view,20f)
    }
}