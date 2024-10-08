package com.app.myrickshawparent.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.myrickshawparent.R
import com.app.myrickshawparent.databinding.ViewImageTitleBinding

class NoDataFound @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {


    private val binding: ViewImageTitleBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.view_image_title, this, true
    )

    init {
        loadAttributes(attrs, defStyleAttr)
    }

    private fun loadAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.NoDataView, defStyleAttr, 0)

        val titleText = typedArray.getString(R.styleable.NoDataView_text)
        val imageRes = typedArray.getResourceId(R.styleable.NoDataView_image_src, 0)
        val isShowImage = typedArray.getBoolean(R.styleable.NoDataView_is_show_image, true)
        val textColor = typedArray.getColor(
            R.styleable.NoDataView_text_color,
            ContextCompat.getColor(context, R.color.black)
        )

        setTitle(titleText)
        isShowImage(isShowImage)
        setImage(imageRes)
        setTitleTextColor(textColor)

        typedArray.recycle()
    }

    fun setTitle(text: String?) {
        binding.tvTitle.text = text ?: ""
    }

    fun isShowImage(isShow: Boolean) {
        if (isShow) {
            binding.ivImage.visibility = VISIBLE
        } else {
            binding.ivImage.visibility = GONE
        }
    }

    fun setImage(imageResId: Int) {
        if (imageResId != 0) {
            binding.ivImage.setImageResource(imageResId)
        }
    }

    fun setTitleTextColor(color: Int) {
        binding.tvTitle.setTextColor(color)
    }
}
