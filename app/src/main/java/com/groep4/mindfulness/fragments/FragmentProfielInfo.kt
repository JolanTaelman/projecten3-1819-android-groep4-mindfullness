package com.groep4.mindfulness.fragments

import android.os.Bundle
import android.os.DropBoxManager
import android.renderscript.Sampler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.groep4.mindfulness.R
import java.security.KeyStore

class FragmentProfielInfo: Fragment() {

   /* lateinit var mAuth: FirebaseAuth
    var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private var txtEmail: TextView? = null
    private var txtNaam: TextView? = null
    private var txtRegio: TextView? = null */


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profiel_info, container, false)
        return view
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val name = user!!.displayName
        txtEmail!!.text = user!!.email
        txtNaam!!.text = user!!.displayName

    }

    fun getUserData() {

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                Log.i("firebase", children.count().toString())

                children.forEach {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase", error!!.message)
            }
        }
    }
*/

}