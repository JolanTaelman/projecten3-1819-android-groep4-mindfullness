package com.groep4.mindfulness.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.ContentValues.TAG
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.groep4.mindfulness.R
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.utils.LoginValidation

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*


 class FragmentLogin : Fragment(){

    /**
     * Houd de inlogtaak bij om ervoor te zorgen dat we deze kunnen annuleren als hierom wordt gevraagd.
     */

    private val RC_SIGN_IN: Int = 1
    lateinit var mAuth: FirebaseAuth
    lateinit var gso : GoogleSignInOptions

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                 .requestEmail()
                 .build()
     }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view:View=inflater!!.inflate(R.layout.fragment_login,container,false)

        // Stel het login formulier in.

        view.password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        view.email
        view.email_sign_in_button.setOnClickListener { attemptLogin() }
        view.email_sign_in_google_button.setOnClickListener{attemptGoogleLogin()}
        activity!!.tv_register.visibility = View.VISIBLE
        activity!!.tv_register.text = "Nog geen account? Klik hier om te registreren."
        activity!!.tv_register.isClickable
        return view
    }


    /**
     *Validatie en aanmelden van account
     */
    private fun attemptLogin() {

        // Reset errors.
        view!!.email.error = null
        view!!.password.error = null

        // values
        val emailStr = view!!.email.text.toString()
        val passwordStr = view!!.password.text.toString()

        var cancel = false
        var focusView: View? = null


        // Controleer op een geldig email adres.
        if (!LoginValidation.isValidEmail(view!!.email)) {

            focusView =view!!.email

            cancel = true
        }

        if (!LoginValidation.isValidPassword(view!!.password)) {

            focusView =view!!.password

            cancel = true
        }

        if (cancel) {

            focusView?.requestFocus()


        } else {

            //Aanmelden
            showProgress(true)
            mAuth = FirebaseAuth.getInstance()

            mAuth.signInWithEmailAndPassword(emailStr,passwordStr)
                    .addOnCompleteListener(activity!!){
                    task ->
                        showProgress(false)
                    activity!!.tv_register.visibility = View.INVISIBLE
                    if (task.isSuccessful()) {

                        //mainactivity tonen
                        val intent = Intent(activity, MainActivity::class.java)
                        this.startActivity(intent)
                        activity!!.finish()

                    } else {

                        activity!!.tv_register.visibility = View.VISIBLE
                        view!!.password.error = this.getString(R.string.error_incorrect_password)
                        view!!.password.requestFocus()
                    }

            }



        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    fun showProgress(show: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            activity!!.login_layout.visibility = if (show) View.GONE else View.VISIBLE
            activity!!.login_layout.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            activity!!.login_layout.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            activity!!.login_progress.visibility = if (show) View.VISIBLE else View.GONE
            activity!!.login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            activity!!.login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            activity!!.login_progress.visibility = if (show) View.VISIBLE else View.GONE
            activity!!.login_layout.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(activity!!.applicationContext, e.toString(), Toast.LENGTH_SHORT)
                // ...
            }
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth.currentUser
                        val intent = Intent(activity, MainActivity::class.java)
                        this.startActivity(intent)
                        activity!!.finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText((activity!!).applicationContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

     /**
      * Inloggen met google
      */
    fun attemptGoogleLogin(){
        val googleSignInClient = GoogleSignIn.getClient(activity!!, gso!!)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


     override fun onResume() {
        super.onResume()
         showProgress(false)
    }

}
