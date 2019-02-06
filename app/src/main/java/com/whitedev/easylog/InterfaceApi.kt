package com.whitedev.easylog

import com.whitedev.easylog.pojo.ApiJerem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InterfaceApi {
    @GET("auth/{uniqueId}")
    fun getUniqueIdAuth(@Path("uniqueId", encoded = true) uniqueId: String?): Call<ApiJerem>
    
    @GET("auth/{login}/{password}")
    fun getLoginAuth(
        @Path("login", encoded = true) login: String, @Path(
            "password",
            encoded = true
        ) password: String
    ): Call<ApiJerem>
    
    @GET("scanqr/{token}/{qrcode}")
    fun getQrCodeStatus(
        @Path("token", encoded = true) token: String, @Path(
            "qrcode",
            encoded = true
        ) qrcode: String
    ): Call<ApiJerem>
    
    @GET("getListMacScanner/{token}")
    fun getListMacScanner(@Path("token", encoded = true) token: String): Call<ApiJerem>
    
}
