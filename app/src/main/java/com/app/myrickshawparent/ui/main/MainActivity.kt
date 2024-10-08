package com.app.myrickshawparent.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivityMainBinding
import com.app.myrickshawparent.ui.home.HomeFragment
import com.app.myrickshawparent.ui.mychild.MyChildFragment
import com.app.myrickshawparent.ui.notification.NotificationFragment
import com.app.myrickshawparent.ui.setting.SettingFragment
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigationMain.clickChange = { position ->
            selectCurrentFragment(position)
        }

        callFragment(HomeFragment(), 0)


    }

    private fun selectCurrentFragment(position: Int) {
        when (position) {
            0 -> {
                callFragment(HomeFragment(), position)
            }

            1 -> {
                callFragment(MyChildFragment(), position)
            }

            2 -> {
                callFragment(NotificationFragment(), position)
            }

            3 -> {
                callFragment(SettingFragment(), position)
            }
        }
    }

    private fun callFragment(fragment: Fragment, position: Int) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragment)
            .commitAllowingStateLoss()

        when (position) {
            0 -> setHeader(FragmentEnum.HOME)
            1 -> setHeader(FragmentEnum.MY_CHILD)
            2 -> setHeader(FragmentEnum.NOTIFICATION)
            3 -> setHeader(FragmentEnum.SETTING)
        }
    }

    private fun setHeader(fragmentEnum: FragmentEnum) {
        when (fragmentEnum) {
            FragmentEnum.HOME -> {
                binding.headerMain.conHomeHeader.visible()
                binding.headerMain.txtOtherTitleHomeHeader.gone()
            }

            FragmentEnum.MY_CHILD -> {
                binding.headerMain.conHomeHeader.gone()
                binding.headerMain.txtOtherTitleHomeHeader.visible()
                binding.headerMain.txtOtherTitleHomeHeader.text =
                    resources.getString(R.string.myChild)
            }

            FragmentEnum.NOTIFICATION -> {
                binding.headerMain.conHomeHeader.gone()
                binding.headerMain.txtOtherTitleHomeHeader.visible()
                binding.headerMain.txtOtherTitleHomeHeader.text =
                    resources.getString(R.string.notification)
            }

            FragmentEnum.SETTING -> {
                binding.headerMain.conHomeHeader.gone()
                binding.headerMain.txtOtherTitleHomeHeader.visible()
                binding.headerMain.txtOtherTitleHomeHeader.text =
                    resources.getString(R.string.setting)
            }
        }

    }
}