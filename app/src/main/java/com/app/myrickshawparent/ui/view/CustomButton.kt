package com.app.myrickshawparent.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.myrickshawparent.R
import com.app.myrickshawparent.databinding.ViewButtonBinding
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.visible
import kotlin.math.roundToInt

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewButtonBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.view_button, this, true
    )

    init {
        loadAttr(attrs, defStyleAttr)
    }

    private fun loadAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(
            attrs, R.styleable.CustomButton, defStyleAttr, 0
        )

        val buttonText = arr.getString(R.styleable.CustomButton_title).toString()
        val loading = arr.getBoolean(R.styleable.CustomButton_is_loading, false)
        val background = arr.getColor(
            R.styleable.CustomButton_button_background,
            ContextCompat.getColor(context, R.color.yellow)
        )
        val textColor = arr.getColor(
            R.styleable.CustomButton_button_text_color,
            ContextCompat.getColor(context, R.color.black)
        )
        val borderColor = arr.getColor(
            R.styleable.CustomButton_button_border_color,
            ContextCompat.getColor(context, R.color.black)
        )
        val borderWidth = arr.getDimension(R.styleable.CustomButton_border_width, 0F)
        val isImage = arr.getBoolean(R.styleable.CustomButton_is_image, false)
        val buttonImage = arr.getResourceId(R.styleable.CustomButton_button_image, 0)
        val buttonTextStyle =
            arr.getResourceId(R.styleable.CustomButton_button_text_style, R.style.ButtonStyleCustom)

        binding.cardViewViewButton.setCardBackgroundColor(background)
        binding.tvTitle.setTextColor(textColor)
        binding.cardViewViewButton.strokeColor = borderColor
        binding.cardViewViewButton.strokeWidth = borderWidth.roundToInt()

        if (isImage) {
            binding.imageViewViewButton.visible()
            binding.imageViewViewButton.setImageResource(buttonImage)
        } else {
            binding.imageViewViewButton.gone()
        }

        binding.tvTitle.setTextAppearance(context, buttonTextStyle)

        arr.recycle()

        setText(buttonText)
        setLoading(loading)
    }

    fun setText(text: String) {
        binding.tvTitle.text = text
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            isEnabled = false
            binding.tvTitle.gone()
            binding.cpLoader.visible()
        } else {
            isEnabled = true
            binding.tvTitle.visible()
            binding.cpLoader.gone()
        }
    }

    fun setButtonBackground(color: Int) {
        binding.cardViewViewButton.setCardBackgroundColor(color)
    }

}