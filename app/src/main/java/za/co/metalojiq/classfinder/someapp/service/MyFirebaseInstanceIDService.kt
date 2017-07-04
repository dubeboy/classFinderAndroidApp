package za.co.metalojiq.classfinder.someapp.service

import android.util.Log
import android.widget.Toast

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity
import za.co.metalojiq.classfinder.someapp.model.StatusRespose

import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.Utils

/**
 * Created by divine on 2017/07/01.
 */

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    private val TAG = "InstanceIDService"

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(refreshedToken: String) {
        saveTokenToPrefs(refreshedToken)
        val apiClient = ApiClient.getClient().create(ApiInterface::class.java)
        val email = Utils.getUserSharedPreferences(this).getString(LoginActivity.LOGIN_PREF_EMAIL, "")
        //assert(email != "")
        if (email != "") {
             apiClient
                    .saveFcmToken(refreshedToken, email)
                    .enqueue(object : Callback<StatusRespose?> {
                        override fun onResponse(call: Call<StatusRespose?>?, response: Response<StatusRespose?>) {
                            Log.d(TAG, "saved token to server")
                            Toast.makeText(this@MyFirebaseInstanceIDService, "Classfinder can now notify you on latest accommodation news", Toast.LENGTH_LONG).show()
                        }

                        override fun onFailure(call: Call<StatusRespose?>?, t: Throwable?) {
                            Toast.makeText(this@MyFirebaseInstanceIDService, "Please connect to the internt to send notifications", Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    fun saveTokenToPrefs(refreshedToken: String) {
        val userSharedPreferences = Utils.getUserSharedPreferences(this)
        val editor = userSharedPreferences.edit()
        editor.putString(FCM_TOKEN, refreshedToken)
        editor.commit()
    }

    companion object {
        val FCM_TOKEN = "fcmToken"
    }
}
