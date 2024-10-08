package com.app.myrickshawparent.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.myrickshawparent.R
import com.app.myrickshawparent.prefrence.MyDataStore
import com.app.myrickshawparent.ui.dialog.CancelDialog
import com.app.myrickshawparent.util.goToLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DialogActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStore: MyDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CancelDialog(
            context = this,
            icon = R.mipmap.ic_richshaw,
            title = getString(R.string.session_expired),
            description = getString(R.string.session_expired_message),
            okButtonText = getString(R.string.ok),
            onOkBtnClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        dataStore.logoutUser()
                    }
                    withContext(Dispatchers.Main) {
                        goToLogin()
                    }
                }
            },
        ).showDialog()

    }
}