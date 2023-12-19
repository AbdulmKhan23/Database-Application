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
import com.squareup.picasso.Picasso
import java.util.UUID

class AddUserActivity : AppCompatActivity() {

    lateinit var addUserBinding: ActivityAddUserBinding

    val database:FirebaseDatabase=FirebaseDatabase.getInstance()//create a database
    val myReference : DatabaseReference=database.reference.child("MyUsers")//reference to a database

    lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    var imageUri :Uri?= null
    val firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReferce: StorageReference = firebaseStorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUserBinding=ActivityAddUserBinding.inflate(layoutInflater)
        val view = addUserBinding.root
        setContentView(view)
        supportActionBar?.title="Add User"
        registerActivityForResult()

        addUserBinding.addButton.setOnClickListener {
            uploadPhoto()
        }
        addUserBinding.userProfile.setOnClickListener{
            selectImage()

        }
    }

    fun selectImage()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val intent = Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            val intent = Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }
    fun  registerActivityForResult(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->
                val resultCode = result.resultCode
                val imageData=result.data
                if(resultCode == RESULT_OK && imageData != null)
                {
                    imageUri=imageData.data
                    imageUri?.let {
                        Picasso.get().load(it).into(addUserBinding.userProfile)
                    }
                }

            })

    }

    fun addUserToDatabase(url:String,imageName:String)
    {
        val name : String = addUserBinding.addName.text.toString()
        val phone_no :Int = addUserBinding.addNumber.text.toString().toInt()
        val email :String = addUserBinding.addEmail.text.toString()


        val id:String= myReference.push().key.toString()//it creates a unique key for each user
        val user = Users(id,name,phone_no,email,url, imageName)
        myReference.child(id).setValue(user).addOnCompleteListener { task ->
            if(task.isSuccessful)
            {
                Toast.makeText(applicationContext,"New User Added To DataBase",Toast.LENGTH_SHORT).show()
                addUserBinding.addButton.isClickable = true
                addUserBinding.progressBarAddUser.visibility= View.INVISIBLE
                finish()
            }
            else
            {
                Toast.makeText(applicationContext,task.exception.toString(),Toast.LENGTH_SHORT).show()

            }
        }
    }
    fun uploadPhoto(){
        addUserBinding.addButton.isClickable = false
        addUserBinding.progressBarAddUser.visibility= View.VISIBLE

        val imageName = UUID.randomUUID().toString()
        val imageReference = storageReferce.child("images").child(imageName)

        imageUri?.let {uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext,"Image Uploaded",Toast.LENGTH_SHORT).show()
                val myUploadedImageReference =storageReferce.child("images").child(imageName)
                myUploadedImageReference.downloadUrl.addOnSuccessListener {url->
                    val imageUrl = url.toString()
                    addUserToDatabase(imageUrl,imageName)
                }

            }.addOnFailureListener{
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        }



    }
}