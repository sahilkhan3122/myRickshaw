package com.app.myrickshawparent.ui.bottomsheet

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseBottomSheetFragment
import com.app.myrickshawparent.databinding.MyImagePickerBottomSheetBinding
import com.app.myrickshawparent.ui.widget.LoadImage
import com.app.myrickshawparent.ui.widget.MyText
import com.app.myrickshawparent.util.printLog
import com.app.myrickshawparent.util.style.text18BlackBold

class MyImagePickerBottomSheet(
    private val list: List<Uri?>,
    private val onImageClick: (Uri) -> Unit,
) :
    BaseBottomSheetFragment<MyImagePickerBottomSheetBinding>(isShowBluer = false) {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): MyImagePickerBottomSheetBinding {
        return MyImagePickerBottomSheetBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        binding.imagePickerBottomSheet.setContent {
            Column {
                MyText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            colorResource(R.color.yellow),
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.allowImageTitle),
                    style = requireContext().text18BlackBold()
                )
                ImagePickerBottomSheet()
            }
        }
    }

    override fun setupOnStart() {

    }

    @Composable
    fun ImagePickerBottomSheet() {
        val context = LocalContext.current
        printLog("TAG", "ImagePickerBottomSheet: $list")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(list) { index, item ->
                LoadImage(imageUrl = item.toString(), modifier = Modifier.aspectRatio(1f)) {
                    if (item != null) {
                        onImageClick.invoke(item)
                        dismiss()
                    }
                }
            }
        }
    }


}