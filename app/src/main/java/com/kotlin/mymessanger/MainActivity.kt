package com.kotlin.mymessanger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //var selectPhotoBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        btnRegister.setOnClickListener {
            performRegister()
        }


        tvAlready.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //selectPhotoBtn = findViewById(R.id.selectPhoto)


        selectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            circulerImage.setImageBitmap(bitmap)

            selectPhoto.alpha = 0f

            val bitmapDrawable = BitmapDrawable(bitmap)
           // selectPhoto.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val userName = etUserName.text.toString()

        if (email.isEmpty() || password.isEmpty()) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if Successful
                Log.d("Main", "Successfully Created user with uid: ${it.result!!.user.uid}")
                showToast("Successfully Created user")
                uploadImagetoFireBaseStorage();
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
                showToast("Failed to create user")
            }
    }

    private fun uploadImagetoFireBaseStorage() {
        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")
        selectedPhotoUri?.let {
            ref.putFile(it)
                .addOnSuccessListener {
                    Log.d("MAinActivity", "upload image ${it.metadata!!.path}")
//                    showToast(it.metadata!!.path)
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("MainActivity","File Location ${it}")
                        saveuserToFirebaseDataBase(it.toString())
                    }
                        .addOnFailureListener {
                            Log.d("MainActivity","File Location Failed")
                        }
                }
                .addOnFailureListener {
                    Log.d("MAinActivity", "upload image Failed")

                }
        }
    }

    private fun saveuserToFirebaseDataBase(imageurl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")

        val user = User(uid, etEmail.text.toString(), imageurl);

        ref.setValue(user)
            .addOnSuccessListener {
                showToast("Saved User into DataBase")
                Log.d("MainActivty","Finaly data Saved into firebase Database online")
            }
            .addOnFailureListener {
                showToast("Saved User into DataBase Failed")
            }

    }
}

class User(val uid: String, val userName: String, val profileImageUrl: String)
