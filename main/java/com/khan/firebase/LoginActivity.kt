package com.khan.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.khan.firebase.databinding.ActivityLoginBinding
import com.khan.firebase.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding=ActivityLoginBinding.inflate(layoutInflater)
        val view= loginBinding.root
        setContentView(view)

        loginBinding.buttonSignUp.setOnClickListener {
            val intent= Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)

        }
        loginBinding.buttonLogIn.setOnClickListener {
            val userEmail=loginBinding.editEmailLogIn.text.toString()
            val userPassword=loginBinding.editPasswordSignIn.text.toString()
            logInWithFirebase(userEmail,userPassword)

        }
        loginBinding.buttonForgot.setOnClickListener {
            val intent = Intent(this,ForgetActivity::class.java)
            startActivity(intent)
        }
        loginBinding.buttonPhoneNumber.setOnClickListener {
            val intent=Intent(this,PhoneActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun logInWithFirebase(userEmail:String,userPassword:String)
    {
        auth.signInWithEmailAndPassword(userEmail,userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,"Login Successful",Toast.LENGTH_SHORT).show()
                    val intent=Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(applicationContext,task.exception?.toString(),Toast.LENGTH_SHORT).show()


                }
            }

    }

    override fun onStart() {
        super.onStart()
        val user=auth.currentUser
        if(user != null)
        {
            val intent=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
        }
    }
}