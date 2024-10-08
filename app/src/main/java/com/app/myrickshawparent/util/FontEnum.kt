package com.app.myrickshawparent.util

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.app.myrickshawparent.R

enum class FontEnum(val fontFamily: FontFamily) {
    BOlD(FontFamily(Font(R.font.itc_avant_garade_std_bold))),
    REGULAR(FontFamily(Font(R.font.itc_avant_garade_std_demi))),
    MEDIUM(FontFamily(Font(R.font.itc_avant_garade_std_md)))
}