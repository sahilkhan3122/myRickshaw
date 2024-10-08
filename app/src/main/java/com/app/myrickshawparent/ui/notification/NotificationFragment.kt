package com.app.myrickshawparent.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseFragment
import com.app.myrickshawparent.databinding.NotificationFragmentBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.notification.response.NotificationItem
import com.app.myrickshawparent.ui.notification.response.NotificationResponse
import com.app.myrickshawparent.ui.widget.MarginVertical
import com.app.myrickshawparent.ui.widget.MyText
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.noRippleClickable
import com.app.myrickshawparent.util.showToastMessage
import com.app.myrickshawparent.util.style.text10GrayBold
import com.app.myrickshawparent.util.style.text12GrayMedium
import com.app.myrickshawparent.util.style.text14BlackBold
import com.app.myrickshawparent.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : BaseFragment<NotificationFragmentBinding>() {

    override val layoutId: Int
        get() = R.layout.notification_fragment

    private lateinit var notificationViewModel: NotificationViewModel

    private lateinit var notificationItemList: MutableList<NotificationItem>

    override fun setupObservable() {

        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        notificationItemList = mutableListOf()

        lifecycleScope.launch {
            notificationViewModel.nfStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {}
                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        requireActivity().showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                        requireActivity().showToastMessage(it.error)
                    }

                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        val data = it.data as NotificationResponse
                        if (data.status) {
                            binding.tvNoNotificationFound.gone()
                            notificationItemList.clear()
                            notificationItemList.addAll(data.notificationList)
                            if (notificationItemList.size != 0) {
                                binding.composeViewNotification.setContent {
                                    SetNotificationData()
                                }
                            } else {
                                requireActivity().showToastMessage(it.data.message)
                            }
                        } else {
                            binding.tvNoNotificationFound.visible()
                        }
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        requireActivity().exceptionHandle(
                            ExceptionData(code = it.code), dataStore = dataStore
                        )
                    }

                }
            }
        }
    }

    @Composable
    private fun SetNotificationData() {
        Column {
            notificationItemList.forEachIndexed { index, notificationItem ->
                NotificationView(notificationItem)
                if (index == notificationItemList.size - 1) {
                    MarginVertical(height = 96.dp)
                } else {
                    MarginVertical(height = 12.dp)
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun NotificationView(
        notificationItem: NotificationItem = NotificationItem(
            "1",
            "Reached School",
            "Lorem Ipsum Is Simply Dummy Text Of The Printing And Typesetting Industry.",
            "08:20 AM"
        ),
        onNotificationClicked: () -> Unit = {},
    ) {
        val context = LocalContext.current
        Row(modifier = Modifier
            .noRippleClickable {
                onNotificationClicked.invoke()
            }
            .fillMaxWidth()
            .background(
                colorResource(R.color.grayOpacity60), RoundedCornerShape(25.dp)
            )
            .padding(20.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(colorResource(R.color.white), shape = CircleShape)
                    .size(56.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_demo_notification),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)
            ) {
                MyText(
                    text = notificationItem.title,
                    modifier = Modifier,
                    style = context.text14BlackBold()
                )
                MarginVertical()
                MyText(
                    text = notificationItem.message,
                    modifier = Modifier,
                    style = context.text12GrayMedium().copy(lineHeight = 15.sp)
                )
                MarginVertical(height = 8.dp)
                MyText(
                    text = notificationItem.date,
                    modifier = Modifier,
                    style = context.text10GrayBold()
                )
            }
        }
    }

}