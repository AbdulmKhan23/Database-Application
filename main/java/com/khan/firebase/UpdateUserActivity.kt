package com.khan.firebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.khan.firebase.databinding.ActivityAddUserBinding
import com.khan.firebase.databinding.ActivityUpdateUserBinding
import com.squareup.picasso.Picasso
import java.util.UUID

class UpdateUserActivity : AppCompatActivity() {
    lateinit var updateUserBinding: ActivityUpdateUserBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference : DatabaseReference =database.reference.child("MyUsers")
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri : Uri?= null
    val firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReferce: StorageReference = firebaseStorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateUserBinding=ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = updateUserBinding.root
        setContentView(view)

        supportActionBar?.title="Update User"
        registerActivityForResult()

        getAndSetData()
        updateUserBinding.updateButton.setOnClickListener {
               uploadPhoto()
        }
        updateUserBinding.userUpdateProfile.setOnClickListener{
            selectImage()

        }
    }

    fun getAndSetData(){

        val name= intent.getStringExtra("name")
        val age=intent.getIntExtra("Age",0).toString()
        val email=intent.getStringExtra("email")
        val imageUrl=intent.getStringExtra("imageUrl").toString()
        Picasso.get().load(imageUrl).into(updateUserBinding.userUpdateProfile)



        updateUserBinding.updateName.setText(name)
        updateUserBinding.updateNumber.setText(age)
        updateUserBinding.updateEmail.setText(email)
    }
    fun selectImage()
    {

            val intent = Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)


    }
    fun  registerActivityForResult(){
        activityResultLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->
                val resultCode = result.resultCode
                val imageData=result.data
                if(resultCode == RESULT_OK && imageData != null)
                {
                    imageUri=imageData.data
                    imageUri?.let {
                        Picasso.get().load(it).into(updateUserBinding.userUpdateProfile)
                    }
                }

            })

    }

    fun updateData(imageUrl:String,imageName:String)
    {
        val updateName=updateUserBinding.updateName.text.toString()
        val updateAge=updateUserBinding.updateNumber.text.toString().toInt()
        val updateEmail=updateUserBinding.updateEmail.text.toString()
        val userId=intent.getStringExtra("id").toString()

        val userMap= mutableMapOf<String,Any>()
        userMap["userID"]=userId
        userMap["userName"]=updateName
        userMap["userAge"]=updateAge
        userMap["userEmail"]=updateEmail
        userMap["url"]=imageUrl
        userMap["imageName"]=imageName

        myReference.child(userId).updateChildren(userMap).addOnCompleteListener{task ->

            if (task.isSuccessful)
            {
                Toast.makeText(applicationContext,"Succcessfully Update",Toast.LENGTH_SHORT).show()
                updateUserBinding.updateButton.isClickable = true
                updateUserBinding.progressBarUpdateUser.visibility= View.INVISIBLE
                finish()
            }

        }



    }
    fun uploadPhoto(){
        updateUserBinding.updateButton.isClickable = false
        updateUserBinding.progressBarUpdateUser.visibility= View.VISIBLE

        val imageName =intent.getStringExtra("imageName").toString()
        val imageReference = storageReferce.child("images").child(imageName)

        imageUri?.let {uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext,"Image Updated",Toast.LENGTH_SHORT).show()
                val myUploadedImageReference =storageReferce.child("images").child(imageName)
                myUploadedImageReference.downloadUrl.addOnSuccessListener {url->
                    val imageUrl = url.toString()
                    updateData(imageUrl,imageName)
                }

            }.addOnFailureListener{
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        }



    }
}