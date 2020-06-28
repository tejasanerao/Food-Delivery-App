package com.internshala.eatsup2.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.eatsup2.R
import com.internshala.eatsup2.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmNewPassword: EditText
    lateinit var btnResetPassSubmit: Button
    lateinit var sharedPreferences: SharedPreferences
    var mobile: String? = "m"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnResetPassSubmit = findViewById(R.id.btnResetPassSubmit)

        btnResetPassSubmit.setOnClickListener {
            val otp = etOTP.text.toString()
            val newpass = etNewPassword.text.toString()
            val confirmnewpass = etConfirmNewPassword.text.toString()

            if (intent != null) {
                mobile = intent.getStringExtra("mobile")
            }

            if (newpass != confirmnewpass||newpass.length<4) {
                val dialog = android.app.AlertDialog.Builder(this@ResetPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Please check password parameters!")
                dialog.setPositiveButton("Ok") { text, listener ->
                }
                dialog.create()
                dialog.show()
            } else {
                val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("password", newpass)
                jsonParams.put("otp", otp)

                if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
                    val jsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                                    dialog.setTitle("Success")
                                    dialog.setMessage("Password Changed Successfully")
                                    dialog.setPositiveButton("Go to Login") { text, listener ->
                                        val intent = Intent(
                                            this@ResetPasswordActivity,
                                            LoginActivity::class.java
                                        )
                                        sharedPreferences.edit().clear().apply()
                                        startActivity(intent)
                                        finish()
                                    }
                                    dialog.create()
                                    dialog.show()
                                } else {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "Incorrect Values",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "Some Error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Error Fetching Data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["content-type"] = "application/json"
                            headers["token"] = "9e730f1070405c"
                            return headers
                        }

                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

    }
}
