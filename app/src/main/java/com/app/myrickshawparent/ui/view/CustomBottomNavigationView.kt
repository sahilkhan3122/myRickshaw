package com.app.myrickshawparent.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.myrickshawparent.R
import com.app.myrickshawparent.util.FontEnum
import com.app.myrickshawparent.ui.widget.MarginVertical
import com.app.myrickshawparent.ui.widget.MyText
import com.app.myrickshawparent.util.noRippleClickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CustomBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {

    var clickChange: ((Int) -> Unit)? = null

    private var isUserLogin = false
    var selectedIndex = mutableIntStateOf(0)

    private val items = arrayListOf(
        NavigationItem(
            R.drawable.ic_home,
            context.getString(R.string.home), NavigationItemType.HOME, isSelected = true
        ),
        NavigationItem(
            R.drawable.ic_my_child,
            context.getString(R.string.myChild),
            NavigationItemType.CATEGORY
        ),
        NavigationItem(
            R.drawable.ic_notification,
            context.getString(R.string.notification),
            NavigationItemType.CART
        ),
        NavigationItem(
            R.drawable.ic_setting,
            context.getString(R.string.setting),
            NavigationItemType.PROFILE
        ),
    )

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                isUserLogin = true
            }
        }
        CustomNavigationBar(items) { selectedIndex ->
            clickChange?.invoke(selectedIndex)
        }
    }

    fun selectFragment(position: Int) {
        selectedIndex.intValue = position
    }

    @Composable
    fun NavigationItem(
        icon: Int,
        label: String,
        navigationItemType: NavigationItemType,
        isSelected: Boolean,
        onClick: () -> Unit,
    ) {
        val visibilityAnim = remember {
            Animatable(initialValue = if (isSelected) 1f else 0f)
        }

        Column(
            modifier = Modifier
                .padding(top = 15.dp)
                .noRippleClickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = R.string.app_name),
                tint = if (isSelected) colorResource(R.color.black) else Color.Gray
            )
            MarginVertical()
            MyText(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) colorResource(R.color.black) else Color.Gray,
                fontSize = 12.sp,
                fontFamily = FontEnum.MEDIUM,
                modifier = Modifier
            )
            MarginVertical()
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .background(
                            colorResource(R.color.yellow),
                            RoundedCornerShape(5.dp)
                        )
                        .width(18.dp)
                        .height(4.dp)
                )
            }
        }

        LaunchedEffect(isSelected) {
            visibilityAnim.animateTo(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(durationMillis = 200)
            )
        }
    }

    @Composable
    fun CustomNavigationBar(
        items: MutableList<NavigationItem>,
        onItemSelected: (Int) -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 40.dp))
                .background(color = colorResource(R.color.lightYellow)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, item ->
                NavigationItem(
                    item.icon,
                    item.label,
                    item.navigationItemType,
                    selectedIndex.intValue == index,
                    onClick = {
                        handleNavigation(index) {
                            onItemSelected(index)
                        }
                    }
                )
            }
        }
    }

    private fun handleNavigation(index: Int, onItemSelected: (Int) -> Unit) {
        if (selectedIndex.intValue != index) {
            selectedIndex.intValue = index
            onItemSelected(index)
        }
    }

    private fun Modifier.conditional(
        condition: Boolean,
        ifTrue: Modifier.() -> Modifier,
        ifFalse: Modifier.() -> Modifier,
    ): Modifier = if (condition) then(ifTrue(Modifier)) else ifFalse(Modifier)

    data class NavigationItem(
        val icon: Int,
        val label: String,
        val navigationItemType: NavigationItemType,
        var isSelected: Boolean = false,
    )

    enum class NavigationItemType {
        HOME,
        CATEGORY,
        CART,
        PROFILE
    }

}
