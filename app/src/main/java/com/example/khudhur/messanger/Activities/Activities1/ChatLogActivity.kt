package com.example.khudhur.messanger.Activities.Activities1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.khudhur.messanger.Activities.Utilities.ChatMessage
import com.example.khudhur.messanger.Activities.Utilities.User
import com.example.khudhur.messanger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.item_chat_from_row.view.*
import kotlinx.android.synthetic.main.item_chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerViewChatLog.adapter = adapter
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.theusername

//        setDummyData()
        listenForMessage()
        sendButton.setOnClickListener {
            sendingMessage()
        }

    }
    private fun listenForMessage(){
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

       val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
              val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
//                    val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                    val currentUser = LaitestMessagesActivity.currentUser


                    adapter.add(ChatToItem(chatMessage.text,currentUser!!))

                }else {
//                    val currentUser = LaitestMessagesActivity.currentUser
                                       val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

                    adapter.add(ChatFromItem(chatMessage.text,toUser!!))

                }
                recyclerViewChatLog.scrollToPosition(adapter.itemCount -1)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }


    private fun sendingMessage(){
//        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()

        val text = sendMessageEditText.text.toString()
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid

        if (fromId == null) {
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessgae = ChatMessage(ref.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        ref.setValue(chatMessgae).addOnSuccessListener {
            sendMessageEditText.text.clear()
        }
        toref.setValue(chatMessgae)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messgae/$fromId/$toId")
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messgae/$toId/$fromId")
        latestMessageRef.setValue(chatMessgae)
        latestMessageToRef.setValue(chatMessgae)

    }

}
class ChatFromItem (val text : String,val user : User): Item<ViewHolder>(){
    override fun getLayout(): Int {
            return R.layout.item_chat_from_row

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.messageFromChatLogTextView.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.chatFromImageView
        Picasso.get().load(uri).into(targetImageView)
    }

}
class ChatToItem (val text : String, val user : User ): Item<ViewHolder>(){
    override fun getLayout(): Int {
            return R.layout.item_chat_to_row

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.messageToChatLogTextView.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.chatToImageView
        Picasso.get().load(uri).into(targetImageView)
    }

}
