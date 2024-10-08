package com.app.myrickshawparent.ui.faq

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivityFaqBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.faq.item.FaqItem
import com.app.myrickshawparent.ui.faq.item.FaqResponse
import com.app.myrickshawparent.ui.widget.MarginVertical
import com.app.myrickshawparent.ui.widget.MyText
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.noRippleClickable
import com.app.myrickshawparent.util.showToastMessage
import com.app.myrickshawparent.util.style.text12GrayMedium
import com.app.myrickshawparent.util.style.text14BlackDemi
import com.app.myrickshawparent.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FaqActivity : BaseActivity<ActivityFaqBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_faq

    private lateinit var faqList: MutableList<FaqItem>
    private lateinit var faqViewModel: FaqViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        faqViewModel = ViewModelProvider(this)[FaqViewModel::class.java]

        binding.header.mcvArrow myClick {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.header.tvTittle.text = getString(R.string.faqs)

        faqList = mutableListOf()

        lifecycleScope.launch {
            faqViewModel.faqStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {}
                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                        showToastMessage(it.error)
                    }

                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        loadingDialog.dismiss()
                        val data = it.data as FaqResponse
                        if (data.status) {
                            faqList.addAll(data.faqList)
                            if (faqList.size != 0) {
                                binding.composeViewFaq.setContent {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(14.dp),
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    ) {
                                        itemsIndexed(faqList) { index, item ->
                                            if (index == 0) {
                                                MarginVertical(height = 16.dp)
                                            }
                                            FaqView(item)
                                            if (index == faqList.lastIndex) {
                                                MarginVertical(height = 14.dp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                binding.noDataFoundFaqs.visible()
                            }
                        } else {
                            binding.noDataFoundFaqs.visible()
                        }
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        exceptionHandle(ExceptionData(code = it.code), dataStore = dataStore)
                    }
                }
            }
        }


    }

    @Composable
    @Preview(showBackground = true)
    private fun FaqView(
        faqItem: FaqItem = FaqItem(
            "Lorem Ipsum is simply dummy text of the printing",
            "Lorem Ipsum is simply dummy text of the printing"
        ),
    ) {
        val visible = remember { mutableStateOf(false) }
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (visible.value) colorResource(R.color.lightYellow) else colorResource(
                        R.color.grayOpacity60
                    ), RoundedCornerShape(20.dp)
                )
                .padding(18.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    visible.value = !visible.value
                }) {
                MyText(
                    text = faqItem.question,
                    style = context.text14BlackDemi(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                )
                Icon(
                    painter = if (visible.value) painterResource(id = R.drawable.ic_arrow_up) else painterResource(
                        id = R.drawable.ic_arrow_down
                    ),
                    contentDescription = stringResource(R.string.app_name),
                    tint = colorResource(R.color.yellow),
                    modifier = Modifier
                        .background(
                            colorResource(R.color.black),
                            CircleShape
                        )
                        .size(24.dp)
                        .padding(6.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            AnimatedContent(
                targetState = visible.value, label = "",
            ) { targetState ->
                if (targetState) {
                    Column {
                        MarginVertical(height = 16.dp)
                        HtmlCompat.fromHtml(faqItem.answer, HtmlCompat.FROM_HTML_MODE_LEGACY).also {
                            MyText(text = it.toString(), style = context.text12GrayMedium())
                        }
                    }
                }
            }
        }
    }

}