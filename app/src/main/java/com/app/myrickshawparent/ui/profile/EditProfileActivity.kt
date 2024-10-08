package com.app.myrickshawparent.ui.profile

import android.Manifest
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivityEditProfileBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.bottomsheet.BottomSheetImagePicker
import com.app.myrickshawparent.ui.bottomsheet.MyImagePickerBottomSheet
import com.app.myrickshawparent.ui.dialog.CancelDialog
import com.app.myrickshawparent.ui.login.response.LoginResponse
import com.app.myrickshawparent.util.PathUtil
import com.app.myrickshawparent.util.applyFocusChangeTextColor
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.openSetting
import com.app.myrickshawparent.util.showToastMessage
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_edit_profile

    private var isGallery: Boolean = false
    private var tempCamaraPath = ""
    private lateinit var launcherPermission: ActivityResultLauncher<String>
    private lateinit var launcherPermissionMultiple: ActivityResultLauncher<Array<String>>
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    lateinit var viewModel: EditProfileViewModel
    private var isUpdateProfile: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isUpdateProfile) {
                        setResult(RESULT_OK)
                    }
                    finish()
                }
            })

        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        binding.viewModel = viewModel

        CoroutineScope(Dispatchers.Main).launch {
            initFun()
        }

        initObserver()

        //Photo picker
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.profilePath = PathUtil.getPath(this, uri).toString()
                    setUserImage()
                }
            }

        /**
         * Permission handler
         * @param isGallery  check user select gallery or camera
         */
        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (isGallery) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        val result = generateImageUrl()
                        val uri = result.first
                        tempCamaraPath = result.second
                        takePicture.launch(uri)
                    }
                } else {
                    val showRationale: Boolean = if (isGallery) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) else shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        )
                    }
                    if (!showRationale) {
                        CancelDialog(
                            context = this,
                            icon = R.mipmap.ic_richshaw,
                            title = getString(R.string.setting),
                            description = getString(R.string.setting_des),
                            okButtonText = getString(R.string.ok),
                            cancelButtonText = getString(R.string.cancel),
                            onOkBtnClick = {
                                openSetting()
                            },
                            isShowCancelButton = true
                        ).showDialog()
                    } else {
                        showToastMessage(resources.getString(R.string.permission))
                    }
                }
            }

        /**
        Multiple permission check
         */
        launcherPermissionMultiple =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->

                if (isGranted.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false)) {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else if (isGranted.getOrDefault(
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                        false
                    )
                ) {
                    val uri = getGrantedImageUris()
                    if (uri.isNotEmpty()) {
                        val bottomSheetFragment = MyImagePickerBottomSheet(list = uri) { it ->
                            viewModel.profilePath = PathUtil.getPath(this, it).toString()
                            setUserImage()
                        }
                        bottomSheetFragment.show(
                            supportFragmentManager, bottomSheetFragment.tag
                        )
                    } else {
                        showToastMessage(resources.getString(R.string.allowImageMsg))
                    }
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) && shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                        )
                    ) {
                        showToastMessage(resources.getString(R.string.permission))
                    } else {
                        CancelDialog(
                            context = this,
                            icon = R.mipmap.ic_richshaw,
                            title = getString(R.string.setting),
                            description = getString(R.string.setting_des),
                            okButtonText = getString(R.string.ok),
                            cancelButtonText = getString(R.string.cancel),
                            onOkBtnClick = {
                                openSetting()
                            },
                            isShowCancelButton = true
                        ).showDialog()
                    }
                }

            }


        //Camera save image success
        takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success) {
                    viewModel.profilePath = tempCamaraPath
                    setUserImage()
                }
            }

    }

    private fun getGrantedImageUris(): List<Uri?> {
        val imagesList = mutableListOf<Uri?>()

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor: Cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imagesList.add(contentUri)
            }
        }
        return imagesList
    }

    private suspend fun initFun() {

        setUserInfo(
            dataStore.getStringData(dataStore.fullName).first(),
            dataStore.getStringData(dataStore.email).first(),
            dataStore.getStringData(dataStore.phone).first()
        )
        viewModel.profilePath = dataStore.getStringData(dataStore.profile).first()
        setUserImage()

        binding.apply {
            header.tvTittle.text = getString(R.string.edit_profile)
            edtName.applyFocusChangeTextColor()
            header.mcvArrow myClick {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.btnSubmit myClick {
            viewModel.editProfileApi()
        }

        binding.imgUserSetting myClick {
            openBottomSheet()
        }

        binding.viewEditEditProfile myClick {
            openBottomSheet()
        }
    }

    private fun setUserImage() {
        Glide.with(this).load(viewModel.profilePath).into(binding.imgUserSetting)
    }

    private fun setUserInfo(name: String, email: String, number: String) {
        binding.edtName.setText(name)
        binding.edtEmail.setText(email)
        binding.edtNumber.setText(number)
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.editProStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {}
                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        loadingDialog.dismiss()
                        val data = it.data as LoginResponse
                        showToastMessage(data.message, data.status)
                        if (data.status) {
                            isUpdateProfile = true
                            dataStore.setStringData(
                                dataStore.fullName,
                                data.data?.fullName.toString()
                            )
                            dataStore.setStringData(
                                dataStore.email,
                                data.data?.email.toString()
                            )
                            dataStore.setStringData(
                                dataStore.phone,
                                data.data?.phoneNumber.toString()
                            )
                            dataStore.setStringData(
                                dataStore.profile,
                                data.data?.userProfile.toString()
                            )
                            setUserInfo(
                                data.data?.fullName.toString(),
                                data.data?.email.toString(),
                                data.data?.phoneNumber.toString()
                            )
                            viewModel.profilePath = data.data?.userProfile.toString()
                            setUserImage()
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

    private fun openBottomSheet() {
        val bottomSheetFragment = BottomSheetImagePicker(onCameraClick = {
            isGallery = false
            launcherPermission.launch(Manifest.permission.CAMERA)
        }, onGalleryClick = {
            isGallery = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                launcherPermissionMultiple.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    )
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcherPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        })
        bottomSheetFragment.show(
            supportFragmentManager, bottomSheetFragment.tag
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}