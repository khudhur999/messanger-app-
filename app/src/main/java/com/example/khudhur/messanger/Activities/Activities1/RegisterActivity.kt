package com.example.khudhur.messanger.Activities.Activities1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.khudhur.messanger.Activities.Utilities.User
import com.example.khudhur.messanger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton.setOnClickListener {
        register()
        }
        alreadyHaveAnAcoountTextView.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }
        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
    var  selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null  ){
            selectedPhotoUri  =  data.data
//            val resolver = activity!!.contentResolver
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri )

            selectPhotoImageView.setImageBitmap(bitmap)
            selectPhotoButton.alpha = 0f
//            val bitMapDrawble = BitmapDrawable(bitmap)
//            selectPhotoButton.setBackgroundDrawable(bitMapDrawble)
        }
    }



    private fun register(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (email.isEmpty() && password.isEmpty()){
            Toast.makeText(this,"please enter email and password",Toast.LENGTH_LONG).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    uploadImageToFirebaseStorage()
                    Toast.makeText(this,"done", Toast.LENGTH_LONG).show()


                }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed to signup :  ${it.message}", Toast.LENGTH_LONG).show()

                }

    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null ) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    //                       it.toString()
                    saveUserToFirebaseDatabase(it.toString())

                    Log.d("RegisterActivity","File Location: $it")
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }


    }
//    private fun saveUserToFirebaseDatabase(profileImageUrl:String) {
//        val uid =  FirebaseAuth.getInstance().uid ?:""
//        val ref =  FirebaseDatabase.getInstance().getReference("/users/$uid")
//        val user = User(uid, usernameEditText.text.toString(), profileImageUrl)
//        ref.setValue(user)
//                .addOnSuccessListener {
//                    Log.d("RegisterActivity","Finally we can save the user to firebase database: ")
//                }
//            .addOnFailureListener {
//                Log.d("RegisterActivity","Failed to set value to database: ${it.message}")
//            }
//    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, usernameEditText.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
                val intent = Intent(this, LaitestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

}



