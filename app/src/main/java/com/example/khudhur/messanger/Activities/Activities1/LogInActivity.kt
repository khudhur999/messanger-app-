package com.example.khudhur.messanger.Activities.Activities1

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.khudhur.messanger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        logInButton.setOnClickListener {
          login()
        }
        backToRighesterTextView.setOnClickListener {
            finish()
        }
    }
   private fun login(){
       val email = emailLogInEditText.text.toString()
       val password = passwordLoginEditText.text.toString()
       if (email.isEmpty() && password.isEmpty()){
           Toast.makeText(this,"please enter email and password", Toast.LENGTH_LONG).show()
           return
       }
       FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
               .addOnCompleteListener {
                   if (!it.isSuccessful) return@addOnCompleteListener
                   val intent = Intent(this,
                       LaitestMessagesActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                   startActivity(intent)
               }
               .addOnFailureListener {
                   Toast.makeText(this,"Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()

               }
    }
}
