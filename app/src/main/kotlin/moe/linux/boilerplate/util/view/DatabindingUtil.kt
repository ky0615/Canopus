package moe.linux.boilerplate.util.view

import android.databinding.BindingAdapter
import android.view.View

object DatabindingUtil {

    @BindingAdapter("setVisibility")
    @JvmStatic
    fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}