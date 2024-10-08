package com.app.myrickshawparent.util.style

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.app.myrickshawparent.R
import com.app.myrickshawparent.util.FontEnum


fun Context.text12GrayRegular(): TextStyle {
    return TextStyle(
        fontSize = 12.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.grayDark60)
        ),
        fontFamily = FontEnum.REGULAR.fontFamily
    )
}

fun Context.text12GrayMedium(): TextStyle {
    return TextStyle(
        fontSize = 12.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.grayDark60)
        ),
        fontFamily = FontEnum.MEDIUM.fontFamily
    )
}

fun Context.text10GrayBold(): TextStyle {
    return TextStyle(
        fontSize = 10.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.grayDark60)
        ),
        fontFamily = FontEnum.BOlD.fontFamily
    )
}

fun Context.text14BlackBold(): TextStyle {
    return TextStyle(
        fontSize = 14.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.black)
        ),
        fontFamily = FontEnum.BOlD.fontFamily
    )
}

fun Context.text14BlackDemi(): TextStyle {
    return TextStyle(
        fontSize = 14.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.black)
        ),
        fontFamily = FontEnum.REGULAR.fontFamily
    )
}

fun Context.text18BlackBold(): TextStyle {
    return TextStyle(
        fontSize = 18.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.black)
        ),
        fontFamily = FontEnum.BOlD.fontFamily
    )
}

fun Context.text12BlackBold(): TextStyle {
    return TextStyle(
        fontSize = 12.sp,
        color = Color(
            ContextCompat.getColor(this, R.color.black)
        ),
        fontFamily = FontEnum.BOlD.fontFamily
    )
}