package com.groep4.mindfulness.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.groep4.mindfulness.R
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Gebruiker

class FragmentProfielInfo: Fragment() {

    private var callback: CallbackInterface? = null
    private var txtEmail: TextView? = null
    private var txtNaam: TextView? = null
    private var gebruiker : Gebruiker? = null
    private var txtRegio: TextView? = null
    private var txtTelnr : TextView? = null

    private var btnGegevensWijzigen : Button? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? CallbackInterface
        if (callback == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profiel_info, container, false)

        txtEmail = view.findViewById(R.id.txtEmail)
        txtNaam = view.findViewById(R.id.txtNaam)
        txtTelnr = view.findViewById(R.id.txtTelnr)
        txtRegio = view.findViewById(R.id.txtRegio)

        //Toon de gegevenswijzigenfragment indien op gegevenswijzigen geklikt is
        btnGegevensWijzigen = view.findViewById(R.id.btnGegevensWijzigen)
        btnGegevensWijzigen!!.setOnClickListener {
            // Launch fragment met callback naar activity
            callback?.setFragment(FragmentProfielGegevensWijzigen(), true)
        }

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)


        if (savedInstanceState != null) {
            gebruiker = savedInstanceState!!.getParcelable("gebruiker") as Gebruiker
        }
        else
        {
            gebruiker = (activity as MainActivity).gebruiker
        }

        txtEmail!!.text = gebruiker!!.email
        txtNaam!!.text = gebruiker!!.name
        txtTelnr!!.text = gebruiker!!.telnr
        txtRegio!!.text = gebruiker!!.regio
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)

        outState!!.putParcelable("gebruiker", this.gebruiker)
    }
}