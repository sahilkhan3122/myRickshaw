package com.app.myrickshawparent.ui.bottomsheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.app.myrickshawparent.base.BaseBottomSheetFragment
import com.app.myrickshawparent.databinding.BottomSheetLayoutBinding

class BottomSheetLogout : BaseBottomSheetFragment<BottomSheetLayoutBinding>() {
    var onLogoutClick: (() -> Unit)? = null
    var onCancelClick: (() -> Unit)? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetLayoutBinding {
        return BottomSheetLayoutBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        binding.btnLogout.setOnClickListener {
            onLogoutClick?.invoke()
            dismiss()
        }

        binding.btnBack.setOnClickListener {
            onCancelClick?.invoke()
            dismiss()
        }
    }

    override fun setupOnStart() {
        dialog?.let { bottomSheetDialog ->
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val layoutParams = it.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.setMargins(70, 0, 70, 0) // Adjust the left and right margins
                it.layoutParams = layoutParams
            }
        }
    }

}
