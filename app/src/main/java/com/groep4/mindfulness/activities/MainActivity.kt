package com.groep4.mindfulness.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.groep4.mindfulness.R
import com.groep4.mindfulness.model.Oefening
import com.groep4.mindfulness.model.Sessie
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {


    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the toolbar view inside the activity layout
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)

        var sessies: ArrayList<Sessie> = getSessies()

        // belangrijk key_page mee te geven om juiste fragment te kunnen laden vanuit eenzelfde activity! (Zie AcitivityPage)
        ll_sessies.setOnClickListener {
            val intent = Intent(this, ActivityPage::class.java)
            intent.putExtra("key_page", "sessies")
            intent.putExtra("sessielist", sessies)
            startActivity(intent)
        }

        ll_reminder.setOnClickListener {
            val intent = Intent(this, ActivityPage::class.java)
            intent.putExtra("key_page", "reminder")
            intent.putExtra("sessielist", sessies)
            startActivity(intent)
        }


    }

    // Menu icons are inflated just as they were with actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun getSessies(): ArrayList<Sessie> {
        val sessies: ArrayList<Sessie> = ArrayList()
        val oefeningen: ArrayList<Oefening> = ArrayList()

        // Static oef toevoegen
        for (i in 1..3) {
            val oef = Oefening("Oefening 0$i", "Beschrijving 0$i")
            oefeningen.add(oef)
        }

        // HTTP Request sessies
        val client = OkHttpClient()

        val request = Request.Builder()
                /*.header("Authorization", "token abcd")*/
                .url("http://10.0.2.2:3000/sessies")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "HTTP request failed: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonarray = JSONArray(response.body()!!.string())
                for (i in 0 until jsonarray.length()) {
                    val jsonobject = jsonarray.getJSONObject(i)
                    val naam = jsonobject.getString("naam")
                    val beschrijving = jsonobject.getString("beschrijving")
                    val sessie: Sessie = Sessie(naam, beschrijving, "Info", oefeningen, false)
                    sessies.add(sessie)
                }
            }
        })
        return sessies
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"Account uitgelogd", Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
    }

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

        }
        return super.onOptionsItemSelected(item)
    }

}