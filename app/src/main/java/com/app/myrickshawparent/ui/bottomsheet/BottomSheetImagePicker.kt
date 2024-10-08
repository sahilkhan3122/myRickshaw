package com.app.myrickshawparent.ui.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.myrickshawparent.base.BaseBottomSheetFragment
import com.app.myrickshawparent.databinding.BottomSheetImagePickerBinding

class BottomSheetImagePicker(
    private val onCameraClick: () -> Unit,
    private val onGalleryClick: () -> Unit,
) : BaseBottomSheetFragment<BottomSheetImagePickerBinding>(isShowBluer = false) {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetImagePickerBinding {
        return BottomSheetImagePickerBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        binding.customBottomSheetGallery.onCameraClick = {
            dismiss()
            onCameraClick.invoke()
        }
        binding.customBottomSheetGallery.onGalleryClick = {
            dismiss()
            onGalleryClick.invoke()
        }
    }

    override fun setupOnStart() {

    }

    public fun dialogDismiss(){
        this.dismiss()
    }


}