package com.khan.firebase

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.ims.ImsMmTelManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.Objects.ToStringHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.khan.firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var MainActivityBinding:ActivityMainBinding

    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val reference:DatabaseReference=database.reference.child("MyUsers")

    val userList = ArrayList<Users>()
    val imageNameList= ArrayList<String>()
    lateinit var usersAdapter: UsersAdapter

    val firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = MainActivityBinding.root
        setContentView(view)

        MainActivityBinding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
               val ID =usersAdapter.getUserId(viewHolder.adapterPosition)
                reference.child(ID).removeValue()

                val imageName = usersAdapter.getImageName(viewHolder.adapterPosition)
                val imageReference=storageReference.child("images").child(imageName)
                imageReference.delete()
                Toast.makeText(applicationContext,"User was deleted",Toast.LENGTH_SHORT).show()
            }

        }).attachToRecyclerView(MainActivityBinding.recyclerView)



        retrieveDataFromDatabase()

    }

    fun retrieveDataFromDatabase(){
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(eachuser in snapshot.children){
                    val users = eachuser.getValue(Users::class.java)
                    if(users != null)
                    {
                        println("userId: ${users.userID}")
                        println("userName: ${users.userName}")
                        println("userAge: ${users.userAge}")
                        println("userEmail: ${users.userEmail}")
                        println("********************************")

                        userList.add(users)
                    }
                    usersAdapter=UsersAdapter(this@MainActivity,userList)
                    MainActivityBinding.recyclerView.layoutManager= LinearLayoutManager(this@MainActivity)
                    MainActivityBinding.recyclerView.adapter = usersAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")//what action is taken when there is error retrieving the data.
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_all,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.deleteAll){

            showDialogMessage()
        }
        else if(item.itemId == R.id.SignOut)
        {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialogMessage() {
        val dialogMessage= AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete All User")
        dialogMessage.setMessage("If click yes, all users will be deleted. If you want to delete a single user, you can left or right swipe.")
        dialogMessage.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialogInterface, i ->

            dialogInterface.cancel()
        })
        dialogMessage.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->

            reference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(eachuser in snapshot.children){
                        val users = eachuser.getValue(Users::class.java)
                        if(users != null)
                        {
                            imageNameList.add(users.imageName)
                        }
                        usersAdapter=UsersAdapter(this@MainActivity,userList)
                        MainActivityBinding.recyclerView.layoutManager= LinearLayoutManager(this@MainActivity)
                        MainActivityBinding.recyclerView.adapter = usersAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



            reference.removeValue().addOnCompleteListener{task ->
                if(task.isSuccessful)
                {
                    for (imageName in imageNameList){
                        val imageReference = storageReference.child("images").child(imageName)
                        imageReference.delete()

                    }
                    usersAdapter.notifyDataSetChanged()
                    Toast.makeText(applicationContext,"All Users are Deleted", Toast.LENGTH_SHORT).show()
                }

            }
        })

        dialogMessage.create().show()
    }
}