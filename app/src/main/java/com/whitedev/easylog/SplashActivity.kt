package com.whitedev.easylog

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.whitedev.easylog.pojo.ApiJerem
import com.whitedev.easylog.utils.Constants.Companion.BASE_URL
import com.whitedev.easylog.utils.Constants.Companion.ERROR
import com.whitedev.easylog.utils.Constants.Companion.PREFS_ID
import com.whitedev.easylog.utils.Constants.Companion.REQUEST_PHONE_STATE
import com.whitedev.easylog.utils.Constants.Companion.SUCCESS
import com.whitedev.easylog.utils.Constants.Companion.USER_BASE_URL
import com.whitedev.easylog.utils.Constants.Companion.USER_TOKEN
import com.whitedev.easylog.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashActivity : AppCompatActivity() {
    
    private var deviceId: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        checkBaseUrl()
        handleBtDeviceId(this)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleBtDeviceId(this)
                } else {
                    goToLogin()
                    
                }
                return
            }
        }
    }
    
    fun handleBtDeviceId(context: Context) {
        deviceId = Utils.getBtDeviceId(context)
        checkMacAddress()
    }
    
    private fun checkMacAddress() {
        if (checkConnectivity()) {
            val uniqueID = deviceId
            
            val retrofit = Retrofit.Builder()
                .baseUrl(checkBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            val service = retrofit.create(InterfaceApi::class.java)
            
            val call: Call<ApiJerem> = service.getUniqueIdAuth(uniqueID)
            
            call.enqueue(object : Callback<ApiJerem> {
                override fun onResponse(call: Call<ApiJerem>, response: Response<ApiJerem>) {
                    // generateAuth(response.body().getEmployeeArrayList())
                    Handler().postDelayed({
                        response.body()?.let {
                            if (it.status == SUCCESS) {
                                saveToken(it.token)
                                val i = Intent(this@SplashActivity, ModeActivity::class.java)
                                startActivity(i)
                                finish()
                            } else {
                                goToLogin()
                            }
                        }
                    }, 1500)
                }
                
                override fun onFailure(call: Call<ApiJerem>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, getString(R.string.splash_failure_msg), Toast.LENGTH_SHORT)
                        .show()
                }
            })
            
        } else {
            Utils.showSnackBar("$ERROR: NO CONNECTIVITY", false, activity_splash_layout)
        }
    }
    
    private fun saveToken(token: String?) {
        token?.let {
            val sharedPref = this.getSharedPreferences(PREFS_ID, MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString(USER_TOKEN, it)
                apply()
            }
        }
    }
    
    private fun checkBaseUrl(): String {
        var baseUrl = BASE_URL
        
        getSharedPreferences(PREFS_ID, MODE_PRIVATE).getString(USER_BASE_URL, null)?.let {
            baseUrl = it
        }
        
        return baseUrl
    }
    
    private fun goToLogin() {
        val i = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(i)
        finish()
    }
    
    private fun checkConnectivity(): Boolean {
        return Utils.isInternetconnected(this)
    }
}