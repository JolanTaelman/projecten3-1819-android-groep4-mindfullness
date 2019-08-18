package com.groep4.mindfulness.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.groep4.mindfulness.R
import com.groep4.mindfulness.fragments.*
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.model.Gebruiker
import com.groep4.mindfulness.model.Oefening
import com.groep4.mindfulness.model.Sessie
import com.groep4.mindfulness.utils.ExtendedDataHolder
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import okhttp3.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), CallbackInterface {

    private val BACK_STACK_ROOT_TAG = "root_fragment"
    private val client = OkHttpClient()
    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    var gebruiker : Gebruiker = Gebruiker()
    var sessies: ArrayList<Sessie> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        Logger.addLogAdapter(AndroidLogAdapter())

        // Set gebruiker
       // if(this.gebruiker == null) {
            this.gebruiker = getAangemeldeGebruiker()
        //}

        // Sessies
        sessies = getSessiesFromDB()
        val extras = ExtendedDataHolder.getInstance()
        extras.putExtra("sessielist", sessies)


        //Set no new fragment if there already is one
        if (savedInstanceState == null) {
            setFragment(FragmentMain(), false)
        }
    }

    /**
     * Om fragment te tonen
     */
    override fun setFragment(fragment: Fragment, addToBackstack: Boolean) {
        if (addToBackstack)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder_main, fragment, "pageContent")
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit()
        else
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder_main, fragment, "pageContent")
                    .commit()
    }

    /**
     * ActionMenu aanmaken
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Sessies ophalen
     */
    fun getSessiesFromDB(): ArrayList<Sessie> {
        val tempSessies: ArrayList<Sessie> = ArrayList()

        // HTTP Request sessies

        val docRef = db.collection("Sessies")

        docRef.get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot){
                //val sessieId = Integer.parseInt(document.get("id").toString()),
                val sessie = Sessie(
                       Integer.parseInt(document.get("id").toString()),
                        document.get("naam") as String,
                        document.get("beschrijving") as String,
                        getOefeningen(Integer.parseInt(document.get("id").toString())),
                        document.get("sessieCode") as String
                )
                tempSessies.add(sessie)

            }
        }
        return tempSessies
    }


    /**
     * Aangemelde gebruiker ophalen
     *
     *
     */
    fun getAangemeldeGebruiker() : Gebruiker{
        var gebruiker = Gebruiker()
        val id = mAuth.currentUser!!.uid
        val source = Source.CACHE
        val docRef = db.collection("Gebruiker").document(id)

        docRef.get(source).addOnSuccessListener { documentSnapshot ->
            val ontvangenGebruiker = documentSnapshot.toObject(Gebruiker::class.java)
            gebruiker.uid = documentSnapshot.id
            gebruiker.regio = ontvangenGebruiker!!.regio
            gebruiker.email = ontvangenGebruiker.email
            gebruiker.name = ontvangenGebruiker.name
            gebruiker.telnr = ontvangenGebruiker.telnr
            gebruiker.groepsnr = ontvangenGebruiker.groepsnr
            gebruiker.sessieId = ontvangenGebruiker.sessieId
        }
        return gebruiker
    }

    /**
     * Oefeningen van sessie ophalen
     */
    fun getOefeningen(sessieId: Int): ArrayList<Oefening>{
        val oefeningen: ArrayList<Oefening> = ArrayList()

        val docRef = db.collection("Oefeningen")


        docRef.get().addOnSuccessListener { documentSnapshot ->

            for (document in documentSnapshot){
                    if(document.data.getValue("sId") == sessieId.toString()) {
                    oefeningen.add(Oefening(
                            1,
                            document.data.getValue("naam") as String,
                            document.data.getValue("beschrijving") as String,
                            document.data.getValue("sId").toString().toInt(),
                            document.data.getValue("url") as String,
                            document.data.getValue("mimeType") as String,
                            document.data.getValue("groepen") as String)
                    )
                }
             }
        }
        return oefeningen
    }

    /**
     * Terugkeerknop
     */
    override fun onBackPressed() {
        super.onBackPressed()
    }

    /**
     * gegevens van de gebruiker bewerken
     */
    fun veranderGegevensGebruiker(gebruikersnaam : String, regio : String, telnr : String) {
        gebruiker!!.name = gebruikersnaam
        gebruiker!!.regio = regio
        gebruiker!!.telnr = telnr
    }

    /**
     * gegevens van de gebruiker opslaan
     */
    fun gegevensGebruikerOpslaan()  {
        val id = mAuth.currentUser!!.uid

        val docRef = db.collection("Gebruiker").document(id)

        val gebruikerMap = hashMapOf(
                "email" to gebruiker.email,
                "telnr" to gebruiker.telnr,
                "name" to gebruiker.name,
                "regio" to gebruiker.regio,
                "groepsnr" to gebruiker.groepsnr,
                "sessieId" to gebruiker.sessieId
                )
        docRef.set(gebruikerMap)
        getAangemeldeGebruiker()
    }



    /**
     * Opent menu om naar FragmentProfiel te gaan of uit te loggen
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_logout -> {

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("Wil je uitloggen ?")

                builder.setPositiveButton("Ja"){dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this,"Account uitgelogd", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ActivityLogin::class.java)
                    this.startActivity(intent)
                    finish()
                }

                builder.setNegativeButton("Nee"){dialog,which ->
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()


                return true
            }

            R.id.action_profiel -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder_main, FragmentProfiel(), "pageContent")
                        .addToBackStack(BACK_STACK_ROOT_TAG)
                        .commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Slaat de feedback op
     */
    fun postFeedback(oefening : Oefening, beschrijving : String, score : String ): String {
        var response : String? = null
        val data = hashMapOf(
                "oefId" to oefening.oefenigenId,
                "beschrijving" to beschrijving,
                "score" to score
        )
        val docRef = db.collection("Feedback")
        docRef
                .add(data)
                .addOnSuccessListener { documentReference ->
                    response = "succes"
                }
                .addOnFailureListener { e ->
                    response = "failure"
                }
                return response.orEmpty()
    }



    /**
     * Voegt de unlockte sessie toe aan de gebruiker
     */
    fun sessieUnlocked() {
        gebruiker.sessieId += 1
        gegevensGebruikerOpslaan()
    }
}