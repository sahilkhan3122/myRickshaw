package com.app.myrickshawparent.network.domain

import com.app.myrickshawparent.base.BaseResponse
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.CHILDREN
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.CONTACT_US
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.DELETE_ACCOUNT
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.FORGOT
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.HOME
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.LOGIN
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.RESEND_OTP
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.RESET_PASS
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.UPDATE_PASSWORD
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.UPDATE_PROFILE
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.VERIFY_OTP
import com.app.myrickshawparent.ui.faq.item.FaqResponse
import com.app.myrickshawparent.ui.forgotpass.ForgotPasswordResponse
import com.app.myrickshawparent.ui.home.item.HomeResponse
import com.app.myrickshawparent.ui.login.response.LoginResponse
import com.app.myrickshawparent.ui.mychild.response.ChildrenResponse
import com.app.myrickshawparent.ui.notification.response.NotificationResponse
import com.app.myrickshawparent.ui.setting.item.LogoutResponse
import com.app.myrickshawparent.ui.splash.response.InitResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Url

interface ApiInterface {

    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun login(@FieldMap hashMap: HashMap<String, String>): Response<LoginResponse>

    @GET
    suspend fun initApi(@Url url: String): Response<InitResponse>

    @FormUrlEncoded
    @POST(VERIFY_OTP)
    suspend fun otpVerifyApi(@FieldMap hashMap: HashMap<String, String>): Response<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST(RESEND_OTP)
    suspend fun resendOtpApi(@FieldMap hashMap: HashMap<String, String>): Response<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST(FORGOT)
    suspend fun forgotApi(@FieldMap hashMap: HashMap<String, String>): Response<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST(RESET_PASS)
    suspend fun resetPasswordApi(@FieldMap hashMap: HashMap<String, String>): Response<BaseResponse>

    @GET
    suspend fun logOutApi(@Url url: String): Response<LogoutResponse>

    @FormUrlEncoded
    @POST(DELETE_ACCOUNT)
    suspend fun deleteAccountApi(@FieldMap hashMap: HashMap<String, String>): Response<LogoutResponse>

    @FormUrlEncoded
    @POST(UPDATE_PASSWORD)
    suspend fun updatePasswordApi(@FieldMap hashMap: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(CONTACT_US)
    suspend fun contactUsApi(@FieldMap hashMap: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(CHILDREN)
    suspend fun childrenApi(@FieldMap hashMap: HashMap<String, String>): Response<ChildrenResponse>

    @FormUrlEncoded
    @POST(HOME)
    suspend fun homeApi(@FieldMap hashMap: HashMap<String, String>): Response<HomeResponse>

    @POST(UPDATE_PROFILE)
    @Multipart
    suspend fun editProfileApi(
        @Part image: MultipartBody.Part,
        @PartMap hashMap: HashMap<String, RequestBody>,
    ): Response<LoginResponse>

    @GET
    suspend fun faqApi(@Url url: String): Response<FaqResponse>

    @GET
    suspend fun getNotificationApi(@Url url: String): Response<NotificationResponse>


}