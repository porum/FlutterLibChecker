package io.github.porum.flutterlibchecker.ui.extension

import android.content.res.Resources
import android.util.TypedValue

val Number.dp: Int
  get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
  ).toInt()
