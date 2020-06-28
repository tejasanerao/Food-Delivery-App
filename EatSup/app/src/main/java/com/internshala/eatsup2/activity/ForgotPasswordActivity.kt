package com.internshala.eatsup2.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotMobile: EditText
    lateinit var etForgotMail: EditText
    lateinit var btnSubmit: Button
    lateinit var backfp: ImageView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        etForgotMail = findViewById(R.id.etForgotEmail)
        etForgotMobile = findViewById(R.id.etForgotMobile)
        btnSubmit = findViewById(R.id.btnSubmit)
        backfp = findViewById(R.id.backfp)

        backfp.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSubmit.setOnClickListener {
            val mobile = etForgotMobile.text.toString()
            val mail = etForgotMail.text.toString()

            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",mobile)
            jsonParams.put("email",mail)

            if(ConnectionManager().checkConnectivity(this@ForgotPasswordActivity))
            {
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener
                {
                    try{
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success)
                        {
                            val first = data.getBoolean("first_try")
                            if(first)
                            {
                                val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                println("Mobile in register is: $mobile")
                                intent.putExtra("mobile", mobile)
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                                dialog.setTitle("Reset Password")
                                dialog.setMessage("OTP has been already sent to your mail before. Please Refer that")
                                dialog.setPositiveButton("Ok") { text, listener ->
                                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                    intent.putExtra("mobile", mobile)
                                    startActivity(intent)
                                    finish()
                                }
                                dialog.create()
                                dialog.show()
                            }

                        }
                        else
                        {
                            Toast.makeText(this@ForgotPasswordActivity, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: Exception)
                    {
                        println("error is : $it")
                        Toast.makeText(this@ForgotPasswordActivity, "Some Error occurred", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener
                {
                    println("Error : $it")
                    Toast.makeText(this@ForgotPasswordActivity, "Error Fetching Data", Toast.LENGTH_SHORT).show()
                })
                {
                    override fun getHeaders(): MutableMap<String, String>
                    {
                        val headers = HashMap<String, String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = "9e730f1070405c"
                        return headers
                    }

                }
                queue.add(jsonObjectRequest)
            }
            else
            {
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
}
