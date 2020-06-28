package com.internshala.eatsup2.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.internshala.eatsup2.R

class ProfileFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtProfileName: TextView
    lateinit var txtProfilePhone: TextView
    lateinit var txtProfileMail: TextView
    lateinit var txtProfileAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = this.context!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfileAddress = view.findViewById(R.id.txtProfileAddress)
        txtProfilePhone = view.findViewById(R.id.txtProfilePhone)
        txtProfileMail = view.findViewById(R.id.txtProfileMail)
        txtProfileName.text = sharedPreferences.getString("user_name", null)
        val phone = "+91-"+sharedPreferences.getString("user_mobile_number", null)
        txtProfilePhone.text = phone
        txtProfileMail.text = sharedPreferences.getString("user_email", null)
        txtProfileAddress.text = sharedPreferences.getString("user_address", null)
        return view
    }

}
