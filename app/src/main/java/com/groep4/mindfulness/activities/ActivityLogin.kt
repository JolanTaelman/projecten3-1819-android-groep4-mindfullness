package com.groep4.mindfulness.activities

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

import com.groep4.mindfulness.R
import com.groep4.mindfulness.fragments.FragmentLogin
import com.groep4.mindfulness.fragments.FragmentRegister
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseUser




class ActivityLogin : AppCompatActivity(){

    lateinit var mLoginFragment: FragmentLogin
    lateinit var mRegisterFragment: FragmentRegister
    var tvRegister: TextView?=null
    lateinit var  mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        initFragment()
        showLoginFragment()

        tvRegister = findViewById(R.id.tv_register) as TextView

        tvRegister?.setOnClickListener {

            showRegisterFragment()

        }

    }

    /**
     * Fragment initialiseren
     */
    private fun initFragment() {
        mLoginFragment= FragmentLogin()
        mRegisterFragment= FragmentRegister()
    }

    /**
     * Loginfragment tonen
     */
    private fun showLoginFragment() {

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.left_in,R.anim.left_out)
                .replace(R.id.login_layout,mLoginFragment,FragmentLogin::class.java.name)
                .commit()
    }

    /**
     * Registerfragment tonen
     */
    private fun showRegisterFragment(){

        supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.right_in,R.anim.right_out,R.anim.left_in,R.anim.left_out)
                .replace(R.id.login_layout,mRegisterFragment,FragmentRegister::class.java.name)
                .commit()

    }

    /**
     * Checken of user is ingelogd bij begin van de app. Als deze is ingelogd, start mainActivity
     */
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.getCurrentUser()

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
         }

    }

    /**
     * Terugkeren
     */
    override fun onBackPressed() {
        val count = fragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }
    }




}