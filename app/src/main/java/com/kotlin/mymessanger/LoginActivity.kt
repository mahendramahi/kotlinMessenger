package com.kotlin.mymessanger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button.setOnClickListener {
            val email = etuserName.text.toString()
            val password = etLogPassword.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            //   .addonComp
        }
    }

}