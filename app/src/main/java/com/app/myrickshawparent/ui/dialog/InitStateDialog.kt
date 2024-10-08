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


class InitStateDialog(
    private val context: Context,
    private val icon: Int?,
    private val message: String,
    private val state: Int,
    private val onOkBtnClick: () -> Unit,
    private val onCancelBtnClick: () -> Unit,
) : Dialog(context, R.style.DialogTheme) {
    fun showDialog() {

        this.setDialogTheme()

        val dialogBinding: DialogCancelBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_cancel, null, false
        )
        this.setContentView(dialogBinding.root)


        dialogBinding.let { binding ->
            if (icon != null) {
                binding.ivCancelOrder.visible()
             //   binding.ivCancelOrder.setImageResource(icon)
            } else {
                binding.ivCancelOrder.gone()
            }
            when(state) { // 0 -> Optional Update, 1 -> Force Update, 2 -> Maintenance, 3 -> Regular Flow
                0 -> {
                    binding.tvTitle.text =  context.getString(R.string.update)
                    binding.btOk.setText(context.getString(R.string.update))
                    binding.btCancel.setText(context.getString(R.string.cancel))
                    binding.btCancel.visible()
                }
                1 -> {
                    binding.tvTitle.text =  context.getString(R.string.update)
                    binding.btOk.setText(context.getString(R.string.update))
                    binding.btCancel.gone()
                    binding.btCancel.setText(context.getString(R.string.cancel))
                }
                2 -> {
                    binding.tvTitle.text =  context.getString(R.string.maintenance)
                    binding.btOk.setText(context.getString(R.string.ok))
                    binding.btCancel.setText(context.getString(R.string.cancel))
                    binding.btCancel.gone()
                }
            }

            binding.tvMessage.text = message
            binding.btOk.setOnClickListener {
                this.dismiss()
                onOkBtnClick.invoke()
            }

            binding.btCancel.setOnClickListener {
                this.dismiss()
                onCancelBtnClick.invoke()
            }
        }


        this.show()
    }

    fun dismissDialog() {
        this.dismiss()
    }
}

