package com.groep4.mindfulness.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groep4.mindfulness.R
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.groep4.mindfulness.interfaces.CallbackInterface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.*


class FragmentMain : Fragment() {

    private var callback: CallbackInterface? = null
    var cal = Calendar.getInstance()
    var quoteTekst: TextView? = null


    companion object {
        fun newInstance(): FragmentMain {
            return FragmentMain()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? CallbackInterface
        if (callback == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        // Toolbar
        val toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // FragmentSessieLijst openen
        view.ll_sessies.setOnClickListener {
            view.ll_sessies.isEnabled = false
            callback!!.setFragment(FragmentSessieLijst(), true)
        }

        // FragmentReminder openen
        view.ll_reminder.setOnClickListener {
            view.ll_reminder.isEnabled = false
            callback!!.setFragment(FragmentReminder(), true)
        }

        // FragmentChat openen
        view.ll_contact.setOnClickListener{
            view.ll_contact.isEnabled = false
            callback!!.setFragment(FragmentChat(), true)
        }

        // FragmentKalender openen
        view.ll_kalender.setOnClickListener{
            view.ll_kalender.isEnabled = false
            callback!!.setFragment(FragmentKalender(), true)
        }

        // Quote van de dag tonen
        quoteTekst = view.findViewById(R.id.tv_quote)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        quoteTekst!!.text = randomQuote(day)

        return view
    }

    override fun onResume()
    {
        super.onResume()
        view!!.ll_sessies.isEnabled = true
        view!!.ll_reminder.isEnabled = true
        view!!.ll_contact.isEnabled = true
        view!!.ll_kalender.isEnabled = true
    }

    /**
     * Geeft voor iedere dag een mindfulnessquote terug
     */
    fun randomQuote(i: Int): String{
        var quote = ""

        when (i){
            1 -> quote = "Elk moment is een plaats waar je nog nooit bent geweest."
            2 -> quote = "Met mindfulness leer je je grootste pestkoppen kennen."
            3 -> quote = "In het laten varen van onze perceptie ontstaat ruimte..."
            4 -> quote = "Laat achter wat je was, laat gaan wat moet en laat zijn wat is."
            5 -> quote = "Je hebt een afspraak met het leven, een afspraak met het hier en nu."
            6 -> quote = "Piekeren is net als schommelen, je bent wel bezig, maar je komt niet van je plaats."
            7 -> quote = "Laat gaan wat was, accepteer wat is, omarm wat komt."
            8 -> quote = "De kleine dingen, momenten ? Die zijn niet klein."
            9 -> quote = "Minder streng voor jezelf zijn, is een weg naar meer ontspanning en zelfcompassie."
            10 -> quote = "Omarm het onbekende."
            11 -> quote = "Hoe stiller je wordt, hoe meer je kan horen."
            12 -> quote = "Tijd nemen voor jezelf is een keuze."
            13 -> quote = "Lach, haal adem, neem de tijd."
            14 -> quote = "Het mooie van afstand nemen is dat het je dichterbij inzicht brengt."
            15 -> quote = "Je bent perfect, inclusief al je imperfecties!"
            16 -> quote = "Het leven is er om vandaag van te genieten."
            17 -> quote = "Verander je toekomst ingrijpend door jezelf te worden."
            18 -> quote = "Ik wil niet weten wat ik later word, ik wil weten wat ik nu ben."
            19 -> quote = "Herinner je gisteren, droom van morgen, maar leef vandaag!"
            20 -> quote = "Waar je ook heen gaat, daar ben je."
            21 -> quote = "Volg je hart, want dat klopt"
            22 -> quote = "Piekeren is de verkeerde kant op fantaseren."
            23 -> quote = "Niks moet & niks mag."
            24 -> quote = "Morgen is er weer een dag."
            25 -> quote = "Don't call it a dream, call it a plan."
            26 -> quote = "Zie niet wat je denkt te zien, maar zie wat er is."
            27 -> quote = "Hakuna matata"
            28 -> quote = "In de stilte van het denken, hoor je de antwoorden."
            29 -> quote = "Geluk kan je vermenigvuldigen door het te delen."
            30 -> quote = "Gun jezelf rust, uit rust komt de kracht."
            31 -> quote = "Oordeel niet, verbaas je slechts."
        }
        return quote
    }
}