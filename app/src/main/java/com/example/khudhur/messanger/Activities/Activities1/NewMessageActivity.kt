package com.example.khudhur.messanger.Activities.Activities1

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.khudhur.messanger.Activities.Utilities.User
import com.example.khudhur.messanger.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.item_user.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
//        val adapter = GroupAdapter<ViewHolder>()
//         adapter.add(UserItem())
//         adapter.add(UserItem())
//         adapter.add(UserItem())
//        recyclerView.adapter = adapter

        inputUser()

    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun inputUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if (user != null ){
                        adapter.add(UserItem(user))

                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }



                recyclerView.adapter = adapter
            }

        })
    }
}
class UserItem(val user : User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
    return R.layout.item_user
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.userNameNewMesaasgTextView.text = user.theusername
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.newMessageImageView)


    }

}
