package com.app.myrickshawparent.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import com.app.myrickshawparent.BuildConfig
import com.app.myrickshawparent.R
import com.app.myrickshawparent.databinding.MySnackbarBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.prefrence.MyDataStore
import com.app.myrickshawparent.ui.dialog.CancelDialog
import com.app.myrickshawparent.ui.login.LoginActivity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

fun printLog(tag: String = "Log Information", value: Any) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, "$tag Log =====> 🧐🧐🧐 $value")
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun isRTL(): Boolean {
    return Locale.getDefault().language == "ar"
}

fun Context.failMsg(error: String, msg: String = resources.getString(R.string.wrong)): String {
    return if (BuildConfig.DEBUG) {
        error
    } else {
        msg
    }
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun TextView.setLineSpacing(extraSpacing: Float, lineSpacingMultiplier: Float = 1.0f) {
    this.setLineSpacing(extraSpacing, lineSpacingMultiplier)
}


fun MaterialTextView.setDrawableColor(context: Context, @ColorRes color: Int) {
    compoundDrawablesRelative.filterNotNull().forEach {
        it.colorFilter = PorterDuffColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

fun Context.getDeviceWidth(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getDeviceWidthApi30(this)
    } else {
        getDeviceWidthLegacy(this)
    }
}

fun Context.oldDeviceWidth(): Int {
    val columnWidth: Int
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val point = Point()
    point.x = display.width
    point.y = display.height
    columnWidth = point.x
    return columnWidth
}

@RequiresApi(Build.VERSION_CODES.R)
fun getDeviceWidthApi30(context: Context): Int {
    val windowMetrics: WindowMetrics =
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
    return windowMetrics.bounds.width()
}

fun getDeviceWidthLegacy(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return displayMetrics.widthPixels
}

fun EditText.afterTextChangedWithMinLength(
    minLength: Int,
    delayMillis: Long = 600L,
    onTextChanged: (String) -> Unit,
) {
    val handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            val search = s.toString().trim()
            runnable?.let { handler.removeCallbacks(it) }
            if (search.isEmpty() || search.length >= minLength) {
                runnable = Runnable {
                    onTextChanged(search)  // Call the callback after the delay
                }
                handler.postDelayed(runnable!!, delayMillis)
            }
        }

        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int, after: Int,
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int, count: Int,
        ) {
        }
    })
}

infix fun View.myClick(listener: (View) -> Unit) {
    this.setOnClickListener(listener)
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
infix fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}


fun TextInputEditText.setupBackgroundColorChangeOnFocus(
    focusedColor: Int = getColor(this.context, R.color.blackEdt),
    unfocusedColor: Int = getColor(this.context, R.color.gray),
) {
    this.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        val parentLayout = this.parent.parent as? TextInputLayout
        parentLayout?.boxBackgroundColor = if (hasFocus) {
            focusedColor

        } else {
            unfocusedColor
        }
    }
}


fun EditText.applyFocusChangeTextColor(
    focusedColor: Int = Color.WHITE,
    unfocusedColor: Int = Color.GRAY,
) {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            this.setTextColor(focusedColor)
            this.setHintTextColor(focusedColor)
        } else {
            this.setTextColor(unfocusedColor)
            this.setHintTextColor(unfocusedColor)
        }
    }
}

var isPasswordVisible: Boolean = false
fun TextInputEditText.setDrawableEndClickListener() {
    this.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {

            if (event.rawX >= (this.right - this.compoundDrawables[2].bounds.width())) {
                togglePasswordVisibility(this)
                return@setOnTouchListener true
            }

        }
        return@setOnTouchListener false
    }
}

private fun togglePasswordVisibility(editText: TextInputEditText) {

    if (editText.text.toString().isNotEmpty()) {
        if (isPasswordVisible) {
            // Hide the password
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            setEndDrawable(editText, R.drawable.ic_eye_masked_selector)
        } else {
            // Show the password
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            setEndDrawable(editText, R.drawable.ic_eye_selector)
        }
        // Move the cursor to the end of the text
        editText.setSelection(editText.text?.length ?: 0)

        // Toggle the boolean flag
        isPasswordVisible = !isPasswordVisible
    }
}

private fun setEndDrawable(editText: TextInputEditText, drawableId: Int) {
    val drawable = ContextCompat.getDrawable(editText.context, drawableId)
    editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun Dialog.setDialogTheme() {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    this.setCancelable(false)
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(this.window!!.attributes)
    lp.dimAmount = 0.5f
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    this.window?.setAttributes(lp)
}

fun Context.showToastMessage(message: String, isSuccess: Boolean = false) {
    val mySnackBarBinding: MySnackbarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(this), R.layout.my_snackbar, null, false
    )
    if (isSuccess) {
        mySnackBarBinding.textViewMySnackBar.background =
            ContextCompat.getDrawable(this, R.drawable.bg_snackbar)
    }
    mySnackBarBinding.textViewMySnackBar.text = message
    val toast = Toast(this)
    toast.duration = Toast.LENGTH_LONG
    toast.view = mySnackBarBinding.root
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0) // Set gravity
    toast.show()
}

fun Context.isPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.checkMultiplePermissions(
    vararg permissions: String,
): Boolean {
    val deniedPermissions = permissions.filter { permission ->
        ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED
    }
    return deniedPermissions.isEmpty()
}

fun Context.openSetting() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        )
    )
}

fun Context.bitmapToIcon(icon: Int): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(
        bitmapFromVectorDrawable(
            icon, 100, 100
        )
    )
}

fun Context.bitmapFromVectorDrawable(drawableId: Int, width: Int, height: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable?.setBounds(0, 0, canvas.width, canvas.height)
    drawable?.draw(canvas)
    return bitmap
}


fun Activity.exceptionHandle(exceptionData: ExceptionData, dataStore: MyDataStore? = null) {
    if (exceptionData.code == 401) {
        CancelDialog(
            context = this,
            icon = R.mipmap.ic_richshaw,
            title = getString(R.string.session_expired),
            description = getString(R.string.session_expired_message),
            okButtonText = getString(R.string.ok),
            onOkBtnClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        dataStore?.logoutUser()
                    }
                    withContext(Dispatchers.Main) {
                        goToLogin()
                    }
                }
            },
        ).showDialog()
    }
}

fun Activity.goToLogin() {
    startActivity(Intent(this, LoginActivity::class.java))
    finishAffinity()
}

fun Context.isAppInForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcesses = activityManager.runningAppProcesses
    return runningAppProcesses.any {
        it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}