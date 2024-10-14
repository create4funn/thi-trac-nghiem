package com.example.thitracnghiem.ApiService


import com.example.thitracnghiem.model.UserItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class UserLogin(val email: String, val password: String)
data class UserRegister(val username: String, val email: String, val phone: String, val password: String, val role: Int)

interface AuthService {
    @POST("auth/login")
    fun loginUser(@Body credentials: UserLogin): Call<LoginResponse>

    @POST("auth/register")
    fun registerUser(@Body registration: UserRegister): Call<RegisterResponse>

    @POST("auth/updateFcmToken/{user_id}/{fcmToken}")
    fun updateFcmToken(@Path ("user_id") user_id : Int, @Path ("fcmToken") fcmToken : String) : Call<Void>

    @PUT("auth/updateProfile/{user_id}")
    fun updateProfile(@Path ("user_id") user_id : Int, @Body userItem: UserItem) : Call<Void>

    @GET("auth/getUser/{user_id}")
    fun getUser(@Path ("user_id") user_id: Int) : Call<UserItem>

    @PUT("auth/changePassword/{user_id}")
    fun changePassword(@Path ("user_id") user_id : Int, @Body changePassword: ChangePassword) : Call<Void>
}

data class LoginResponse(val message: String, val token: String)
data class RegisterResponse(val message: String, val userId: Int)
data class ChangePassword(val oldPassword: String, val newPassword: String)
