package com.app.myrickshawparent.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.myrickshawparent.R
import com.app.myrickshawparent.util.noRippleClickable
import com.app.myrickshawparent.util.style.text12BlackBold
import com.app.myrickshawparent.util.style.text18BlackBold
import com.app.myrickshawparent.ui.widget.MarginHorizontal
import com.app.myrickshawparent.ui.widget.MarginVertical
import com.app.myrickshawparent.ui.widget.MyText


class CustomBottomSheetGallery @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {

    var onCameraClick: () -> Unit = {}
    var onGalleryClick: () -> Unit = {}

    @Composable
    override fun Content() {
        BottomSheetImagePickerView()
    }

    @Composable
    fun BottomSheetImagePickerView() {
        val context = LocalContext.current

        Column(

        ) {
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
                text = stringResource(R.string.select_option),
                style = context.text18BlackBold()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            onCameraClick.invoke()
                        }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera_white),
                        contentDescription = stringResource(R.string.app_name),
                        tint = colorResource(R.color.black),
                        modifier = Modifier
                            .background(
                                colorResource(R.color.whiteGray), CircleShape
                            )
                            .padding(20.dp)

                    )
                    MarginVertical(height = 20.dp)
                    MyText(
                        text = stringResource(R.string.take_from_camera),
                        style = context.text12BlackBold()
                    )
                }
                MarginHorizontal()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            onGalleryClick.invoke()
                        }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = stringResource(R.string.app_name),
                        tint = colorResource(R.color.black),
                        modifier = Modifier
                            .background(
                                colorResource(R.color.whiteGray), CircleShape
                            )
                            .padding(20.dp)

                    )
                    MarginVertical(height = 20.dp)
                    MyText(
                        text = stringResource(R.string.select_from_gallery),
                        style = context.text12BlackBold()
                    )
                }

            }
        }

    }

}
