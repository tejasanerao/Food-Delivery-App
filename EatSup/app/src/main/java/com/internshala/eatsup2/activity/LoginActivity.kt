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

class LoginActivity : AppCompatActivity() {

    lateinit var etLoginPhone: EditText
    lateinit var etLoginPassword: EditText
    lateinit var btnLogIn: Button
    lateinit var txtRegisterHere: TextView
    lateinit var txtForgotPassword: TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)

        etLoginPassword = findViewById(R.id.etLoginPassword)
        etLoginPhone = findViewById(R.id.etLoginPhone)
        btnLogIn = findViewById(R.id.btnLogIn)
        txtRegisterHere = findViewById(R.id.txtRegisterHere)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)


        txtForgotPassword.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }


        btnLogIn.setOnClickListener {
            val mobile_number = etLoginPhone.text.toString()
            val password = etLoginPassword.text.toString()

            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result "
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",mobile_number)
            jsonParams.put("password",password)

            if(ConnectionManager().checkConnectivity(this@LoginActivity))
            {
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try{
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success)
                        {
                            val response = data.getJSONObject("data")
                            sharedPreferences.edit()
                                .putString("user_id", response.getString("user_id")).apply()
                            sharedPreferences.edit()
                                .putString("user_name", response.getString("name")).apply()
                            sharedPreferences.edit()
                                .putString("user_mobile_number", response.getString("mobile_number")).apply()
                            sharedPreferences.edit()
                                .putString("user_address", response.getString("address")).apply()
                            sharedPreferences.edit()
                                .putString("user_email", response.getString("email")).apply()
                            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this@LoginActivity, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: Exception)
                    {
                        Toast.makeText(this@LoginActivity, "Some Error occurred", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this@LoginActivity, "Error Fetching Data", Toast.LENGTH_SHORT).show()
                })
                {
                    override fun getHeaders(): MutableMap<String, String> {
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }
        }

        txtRegisterHere.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
