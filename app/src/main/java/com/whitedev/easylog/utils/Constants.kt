package com.whitedev.easylog.utils

class Constants {
    
    companion object {
        const val BASE_URL = "http://api.easylog.w-dev.fr/api/"
        
        const val USER_TOKEN = "USER_TOKEN"
        const val USER_BASE_URL = "USER_BASE_URL"
        
        const val REQUEST_CODE_QR_SCAN = 101
        
        const val REQUEST_PHONE_STATE = 10
        const val REQUEST_READ_CONTACTS = 20
        const val REQUEST_CAMERA_ACCESS = 30
        
        const val PREFS_ID = "PREFS_ID"
        
        const val BASE_TIMER = 1500
        const val BASE_URL_MANUAL = "BASE_URL_MANUAL"
        
        const val ERROR_SAME_ZONE = "TRACKING NUMBER HAS BEEN ALREADY SCAN IN THE SAME ZONE"
        const val ERROR_MISSING_ZONE = "ZONE HAS NOT BEEN SELECTED"
        const val ERROR_MISSING_TRACKING = "TRACKING NUMBER DON'T EXIST"
        const val ERROR_EXPIRED_TOKEN = "TOKEN ERROR"
        const val ENVOYE = "ENVOYE"
        const val ZONE = "ZONE"
        const val FAILURE = "FAILURE"
        const val ERROR = "ERROR"
        
        const val SUCCESS = "SUCCESS"
        const val NOT_CONFIRM = "NON_CONFIRME"
    }
}