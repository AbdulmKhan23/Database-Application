package com.khan.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.khan.firebase.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {

    lateinit var forgetBinding :ActivityForgetBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetBinding=ActivityForgetBinding.inflate(layoutInflater)
        val view = forgetBinding.root
        setContentView(view)

        forgetBinding.buttonReset.setOnClickListener {
            val email= forgetBinding.editTextReset.text.toString()
            auth.sendPasswordResetEmail(email).addOnCompleteListener {task->
                if(task.isSuccessful)
                {
                    Toast.makeText(applicationContext,"Password Reset Email Sent",Toast.LENGTH_SHORT).show()
                    finish()

                }

            }

        }
    }
}