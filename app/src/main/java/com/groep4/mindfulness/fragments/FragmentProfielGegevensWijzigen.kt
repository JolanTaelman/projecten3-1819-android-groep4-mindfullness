package com.groep4.mindfulness.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.groep4.mindfulness.R
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Gebruiker
import okhttp3.FormBody
import java.net.URL

class FragmentProfielGegevensWijzigen : Fragment() {

    private var gebruiker : Gebruiker? = null

    private var btnAnnuleren : Button? = null
    private var btnBevestigen : Button? = null
    private var txtRegio : TextView? = null
    private var txtGebruikersnaam : TextView? = null
    private var txtTelnr : TextView? = null
    private var txtGebruikersnaamFout : TextView? = null
    private var txtTelnrFout : TextView? = null
    private var inflater : LayoutInflater? = null
    private var container : ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View=inflater!!.inflate(R.layout.fragment_profiel_gegevens_wijzigen,container,false)

        this.inflater = inflater
        this.container = container

        btnAnnuleren = view.findViewById(R.id.btnAnnuleren)
        btnBevestigen = view.findViewById(R.id.btnBevestigen)

        txtRegio = view.findViewById(R.id.txtRegio)
        txtGebruikersnaam = view.findViewById(R.id.txtGebruikersnaam)
        txtTelnr = view.findViewById(R.id.txtTelnr)
        txtGebruikersnaamFout = view.findViewById(R.id.txtFoutGebruikersnaam)
        txtTelnrFout = view.findViewById(R.id.txtFoutTelefoonnummer)

        gebruiker = (activity as MainActivity)!!.gebruiker

        txtRegio!!.text = gebruiker!!.regio
        txtGebruikersnaam!!.text = gebruiker!!.name
        txtTelnr!!.text = gebruiker!!.telnr


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnAnnuleren!!.setOnClickListener{
            activity!!.onBackPressed()
        }

        btnBevestigen!!.setOnClickListener {
            var fout : Boolean? = false

            if(txtGebruikersnaam!!.text.toString().length > 3) {
                txtGebruikersnaamFout!!.visibility = View.INVISIBLE
            }
            else {
                fout = true
                txtGebruikersnaamFout!!.visibility = View.VISIBLE
            }
            if(txtTelnr!!.text.isEmpty() || txtTelnr!!.text.length == 10 || txtTelnr!!.text.length == 9)
                txtTelnrFout!!.visibility = View.INVISIBLE
            else {
                fout = true
                txtTelnrFout!!.visibility = View.VISIBLE
            }

            // Als er geen fouten zijn, feedback verzenden
            if(!fout!!) {
                (activity as MainActivity)!!
                        .veranderGegevensGebruiker(txtGebruikersnaam!!.text.toString(),
                                txtRegio!!.text.toString(), txtTelnr!!.text.toString())

                val fromBodyBuilder = FormBody.Builder()
                fromBodyBuilder.add("name", txtGebruikersnaam!!.text.toString())
                fromBodyBuilder.add("regio", txtRegio!!.text.toString())
                fromBodyBuilder.add("telnr", txtTelnr!!.text.toString())
                fromBodyBuilder.add("uid", gebruiker!!.uid)
                fromBodyBuilder.add("email", gebruiker!!.email)
                fromBodyBuilder.add("groepnr", gebruiker!!.groepsnr.toString())
                fromBodyBuilder.add("sessieid", gebruiker!!.sessieId.toString())
                var url = "http://141.134.155.219:3000/users/" + gebruiker!!.uid
                (activity as MainActivity)!!.gegevensGebruikerOpslaan(fromBodyBuilder.build(), url)
                activity!!.onBackPressed()
            }
        }
    }


}