package com.groep4.mindfulness.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.groep4.mindfulness.R
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.model.Message
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.text.DateFormat

class FragmentChat : Fragment(){

    //Layout objecten
    private var listView: ListView? = null
    private var btnSend: View? = null
    private var editText: EditText? = null
    //Firebase objecten
    private var adapter: FirebaseListAdapter<Message>? = null
    private var dbInstance : DatabaseReference? = FirebaseDatabase.getInstance().reference.child("Chat")
    private lateinit var currentUserId: FirebaseUser



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Top bar info instellen
        view.tr_page.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorGreen))
        view.tv_page.setText(R.string.contact)

        currentUserId = FirebaseAuth.getInstance().currentUser!!

        view.contactUser.text = "Psycholoog"

        btnSend = view.findViewById(R.id.btn_chat_send)

        /** Bericht sturen naar de database met als sleutel het userid van de huidig ingelogde gebruiker. */
        btnSend!!.setOnClickListener {
            editText = view.findViewById(R.id.msg_type)
            dbInstance!!.child(currentUserId!!.uid).push().setValue(
                    Message(editText!!.text.toString(),
                            FirebaseAuth.getInstance().currentUser!!.displayName!!))
            editText!!.setText("")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayChatMessages(view)

    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    /**
     * De listAdapter zorgt ervoor dat de List opgevuld raakt met chatberichten opgehaald uit de database met de huidige gebruiker's userid als argument.
     */
    private fun displayChatMessages(view: View) {

        listView = view.findViewById<View>(R.id.list_msg) as ListView

        val options: FirebaseListOptions<Message> = FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.message)
                .setQuery(dbInstance!!.child(currentUserId.uid), Message::class.java)
                .setLifecycleOwner(this)
                .build()


        adapter = object : FirebaseListAdapter<Message>(options) {

            override fun populateView(v: View, model: Message, position: Int) {
                val messageText = v.findViewById(R.id.message_text) as TextView
                val messageUser = v.findViewById(R.id.message_user) as TextView
                val messageTime = v.findViewById(R.id.message_time) as TextView

                val messageBackground = v.findViewById(R.id.message_background) as RelativeLayout
                messageBackground.setBackgroundColor(Color.WHITE)


                /**
                 * Als het bericht in de db niet matcht met de huidige gebruiker, zet de achtergrond blauw.
                 * */
                if(model.messageUser != currentUserId.displayName){
                    messageBackground.setBackgroundColor(Color.parseColor("#9BBBD8"))
                }

                messageText.text = model.content
                messageTime.text = DateFormat.getDateInstance().format(model.messageTime)
                messageUser.text = model.messageUser
            }}

        listView!!.adapter = adapter
    }




}
