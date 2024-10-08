package com.app.myrickshawparent.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.app.myrickshawparent.R
import com.app.myrickshawparent.databinding.DialogCancelBinding
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.setDialogTheme
import com.app.myrickshawparent.util.visible

class CancelDialog(
    private val context: Context,
    private val icon: Int?,
    private val title: String,
    private val description: String,
    private val cancelButtonText: String = "cancel",
    private val okButtonText: String,
    private val onOkBtnClick: () -> Unit,
    private val isShowCancelButton: Boolean = false,
) : Dialog(context, R.style.DialogTheme) {

    fun showDialog() {

        setDialogTheme()

        val dialogBinding: DialogCancelBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_cancel, null, false
        )
        setContentView(dialogBinding.root)

        if (icon != null) {
            dialogBinding.ivCancelOrder.visible()
            dialogBinding.ivCancelOrder.setImageResource(icon)
        } else {
            dialogBinding.ivCancelOrder.gone()
        }
        dialogBinding.tvTitle.text = title
        dialogBinding.tvMessage.text = description
        dialogBinding.btCancel.setText(cancelButtonText)
        dialogBinding.btOk.setText(okButtonText)
        if (isShowCancelButton) {
            dialogBinding.btCancel.visible()
        } else {
            dialogBinding.btCancel.gone()
        }
        dialogBinding.btCancel.setOnClickListener { this.dismiss() }
        dialogBinding.btOk.setOnClickListener {
            this.dismiss()
            onOkBtnClick.invoke()
        }

        this.show()
    }

    fun dismissDialog() {
        this.dismiss()
    }
}