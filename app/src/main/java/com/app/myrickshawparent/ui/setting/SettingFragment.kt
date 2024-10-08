package com.app.myrickshawparent.ui.setting

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseFragment
import com.app.myrickshawparent.databinding.SettingFragmentBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.bottomsheet.BottomSheetLogout
import com.app.myrickshawparent.ui.changepassword.ChangePasswordActivity
import com.app.myrickshawparent.ui.contactus.ContactUsActivity
import com.app.myrickshawparent.ui.faq.FaqActivity
import com.app.myrickshawparent.ui.login.LoginActivity
import com.app.myrickshawparent.ui.profile.EditProfileActivity
import com.app.myrickshawparent.ui.setting.item.LogoutResponse
import com.app.myrickshawparent.ui.setting.item.SettingMenu
import com.app.myrickshawparent.ui.widget.MarginVertical
import com.app.myrickshawparent.ui.widget.SettingMenuView
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.showToastMessage
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingFragmentBinding>() {
    private lateinit var settingViewModel: SettingViewModel
    override val layoutId: Int
        get() = R.layout.setting_fragment

    private lateinit var settingMenuList: MutableList<SettingMenu>

    private var resultLauncherAddAddress =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                CoroutineScope(Dispatchers.Main).launch {
                    setProfileData()
                }
            }
        }

    override fun setupObservable() {
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        binding.viewModel = settingViewModel

        CoroutineScope(Dispatchers.Main).launch {
            setProfileData()
        }

        settingMenuList = mutableListOf(
            SettingMenu(getString(R.string.change_password), SettingEnum.CHANGE_PASSWORD),
            SettingMenu(getString(R.string.contact_us), SettingEnum.CONTACT_US),
            SettingMenu(getString(R.string.faqs), SettingEnum.FAQ)
        )

        binding.imgUserSetting myClick {
            startActivityFun()
        }

        binding.viewEditSetting myClick {
            startActivityFun()
        }


        binding.btnLogoutSetting myClick {
            val bottomSheetLogout = BottomSheetLogout()
            bottomSheetLogout.show(
                requireActivity().supportFragmentManager, bottomSheetLogout.tag
            )
            bottomSheetLogout.onLogoutClick = {
                settingViewModel.logoutApi()
            }
        }

        binding.composeViewMenuSetting.setContent {
            Column {
                settingMenuList.forEachIndexed { index, settingMenu ->
                    SettingMenuView(settingMenu.name) {
                        when (settingMenu.settingEnum) {
                            SettingEnum.CHANGE_PASSWORD -> {
                                val intent = Intent(
                                    context,
                                    ChangePasswordActivity::class.java
                                )
                                startActivity(intent)
                            }

                            SettingEnum.CONTACT_US -> {
                                val intent = Intent(
                                    context,
                                    ContactUsActivity::class.java
                                )
                                startActivity(intent)
                            }

                            SettingEnum.FAQ -> {
                                val intent = Intent(
                                    context,
                                    FaqActivity::class.java
                                )
                                startActivity(intent)
                            }
                        }
                    }
                    if (index != settingMenuList.size - 1) {
                        MarginVertical()
                    }
                }
            }

        }

        dataObservation()

    }

    private fun startActivityFun() {

        resultLauncherAddAddress.launch(
            Intent(
                context,
                EditProfileActivity::class.java
            )
        )
    }

    private suspend fun setProfileData() {
        val profile = dataStore.getStringData(dataStore.profile).first()
        val fullName = dataStore.getStringData(dataStore.fullName).first()
        val email = dataStore.getStringData(dataStore.email).first()
        Glide.with(requireContext()).load(profile)
            .into(binding.imgUserSetting)
        binding.tvUserNameSetting.text = fullName
        binding.tvUserEmailSetting.text = email
    }

    private fun dataObservation() {
        lifecycleScope.launch {
            settingViewModel.logoutStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        requireActivity().showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        loadingDialog.dismiss()
                        if (it.data is LogoutResponse) {
                            if (it.data.status) {
                                requireActivity().showToastMessage(it.data.message, true)
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO){
                                        dataStore.logoutUser()
                                    }
                                    withContext(Dispatchers.Main){
                                        val intent = Intent(
                                            context,
                                            LoginActivity::class.java
                                        )
                                        startActivity(intent)
                                        requireActivity().finishAffinity()
                                    }
                                }

                            } else {
                                requireActivity().showToastMessage(it.data.message)
                            }
                        }
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        requireActivity().exceptionHandle(ExceptionData(code = it.code), dataStore = dataStore)
                    }

                }
            }
        }
    }
}