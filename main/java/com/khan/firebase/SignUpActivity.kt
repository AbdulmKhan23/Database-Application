package com.khan.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.khan.firebase.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var signUpBinding: ActivitySignUpBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            signUpBinding=ActivitySignUpBinding.inflate(layoutInflater)
        val view=signUpBinding.root
        setContentView(view)

        signUpBinding.buttonSign.setOnClickListener {
            val userEmail = signUpBinding.editEmailSignIn.text.toString()
            val userPassword = signUpBinding.editPasswordSignIn.text.toString()
            signUpWithFireBase(userEmail,userPassword)


        }
    }

    fun signUpWithFireBase(userEmail:String, userPassword:String)
    {
        auth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener {task->
            if(task.isSuccessful)
            {
                Toast.makeText(applicationContext,"Account has been created Successfully",Toast.LENGTH_SHORT).show()
                finish()
            }
            else
            {
                Toast.makeText(applicationContext,task.exception?.toString(),Toast.LENGTH_SHORT).show()
            }

        }
    }
}