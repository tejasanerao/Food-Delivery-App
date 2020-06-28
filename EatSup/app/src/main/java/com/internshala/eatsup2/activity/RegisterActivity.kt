package com.internshala.eatsup2.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.eatsup2.R
import com.internshala.eatsup2.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etMail: EditText
    lateinit var etMobile: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etComfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var back: ImageView
    lateinit var check: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etAddress = findViewById(R.id.etAddress)
        etName = findViewById(R.id.etName)
        etMail = findViewById(R.id.etMail)
        etPassword = findViewById(R.id.etPassword)
        etMobile = findViewById(R.id.etMobile)
        etComfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        back = findViewById(R.id.back)


        back.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val mobile_number = etMobile.text.toString()
            val password = etPassword.text.toString()
            val address = etAddress.text.toString()
            val email = etMail.text.toString()

            if ((password != etComfirmPassword.text.toString()) || (mobile_number.length != 10) || name.length < 3||password.length<4) {
                val dialog = AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Please check whether the input parameters are correct")
                dialog.setPositiveButton("Ok") { text, listener ->
                }
                dialog.create()
                dialog.show()
            } else {
                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", mobile_number)
                jsonParams.put("password", password)
                jsonParams.put("address", address)
                jsonParams.put("email", email)
                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                    val jsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val intent =
                                        Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Incorrect Credentials",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Some Error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@RegisterActivity,
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
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            }

        }


    }
}
