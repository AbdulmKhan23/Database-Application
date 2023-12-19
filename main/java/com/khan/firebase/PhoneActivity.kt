package com.khan.firebase

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.khan.firebase.databinding.ActivityPhoneBinding
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    lateinit var phoneActivityBinding:ActivityPhoneBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()
    lateinit var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationCode =""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneActivityBinding=ActivityPhoneBinding.inflate(layoutInflater)
        val view = phoneActivityBinding.root
        setContentView(view)

        phoneActivityBinding.buttonSendOtp.setOnClickListener {
            val userPhoneNumber=phoneActivityBinding.editTextPhoneNumber.text.toString()
            val option=PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(userPhoneNumber)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this@PhoneActivity)
                .setCallbacks(mCallbacks).build()

                PhoneAuthProvider.verifyPhoneNumber(option)

        }
        phoneActivityBinding.buttonVerifyOtp.setOnClickListener {
            signInWithOTP()

        }

        mCallbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                TODO("Not yet implemented")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode=p0
            }

        }
    }
    fun signInWithOTP(){
        val userEnterOtp=phoneActivityBinding.editOTP.text.toString()
        val credential=PhoneAuthProvider.getCredential(verificationCode,userEnterOtp)
        signInWithPhoneAuthCredential(credential)
    }
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential)
    {
        auth.signInWithCredential(credential).addOnCompleteListener{ task->
            if(task.isSuccessful) {
                val intent=Intent(this@PhoneActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(applicationContext,"Incorrect OTP",Toast.LENGTH_SHORT).show()
            }
    }


    }
}