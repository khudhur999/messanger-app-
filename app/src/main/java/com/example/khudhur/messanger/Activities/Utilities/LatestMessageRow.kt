package com.example.khudhur.messanger.Activities.Utilities

import com.example.khudhur.messanger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_latest_message.view.*

class LatestMessageRow(val chatMessage : ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser : User? = null
    override fun getLayout(): Int {
        return R.layout.item_latest_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latestMessageTextView.text = chatMessage.text
        val chatPartnerId : String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser =   snapshot.getValue(User::class.java)
                viewHolder.itemView.usernameTextView.text = chatPartnerUser?.theusername
                val image = viewHolder.itemView.latestMessageImageView
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(image)


            }

        })



    }

}
