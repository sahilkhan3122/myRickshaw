package com.app.myrickshawparent.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.myrickshawparent.R
import com.app.myrickshawparent.util.FontEnum
import com.app.myrickshawparent.util.noRippleClickable

@Composable
@Preview(showBackground = true)
fun SettingMenuView(name: String = "Change Password", onSettingMenuClicked: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .noRippleClickable {
                onSettingMenuClicked()
            }
            .background(colorResource(R.color.grayOpacity60), RoundedCornerShape(40.dp))
            .fillMaxWidth()
            .padding(22.dp)
    ) {
        MyText(
            text = name,
            fontSize = 14.sp,
            fontFamily = FontEnum.REGULAR,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Icon(
            painter = painterResource(R.drawable.ic_right_arrow), contentDescription = "",
            modifier = Modifier.padding(start = 10.dp)
        )

    }
}