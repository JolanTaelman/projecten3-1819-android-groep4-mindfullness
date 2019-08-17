package com.groep4.mindfulness.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.groep4.mindfulness.R
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Gebruiker

class FragmentProfielOverzicht: Fragment() {

    private var gebruiker : Gebruiker? = null
    private var buszetels : ArrayList<ImageView> = ArrayList()

    private var txtHuidigeSessie : TextView? = null
    private var busSeat1 : ImageView? = null
    private var busSeat2 : ImageView? = null
    private var busSeat3 : ImageView? = null
    private var busSeat4 : ImageView? = null
    private var busSeat5 : ImageView? = null
    private var busSeat6 : ImageView? = null
    private var busSeat7 : ImageView? = null
    private var busSeat8 : ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profiel_overzicht, container, false)

        txtHuidigeSessie = view.findViewById(R.id.txtHuidigeSessie)

        busSeat1 = view.findViewById(R.id.iv_bus_seat1)
        buszetels.add(busSeat1!!)

        busSeat2 = view.findViewById(R.id.iv_bus_seat2)
        buszetels.add(busSeat2!!)

        busSeat3 = view.findViewById(R.id.iv_bus_seat3)
        buszetels.add(busSeat3!!)

        busSeat4 = view.findViewById(R.id.iv_bus_seat4)
        buszetels.add(busSeat4!!)

        busSeat5 = view.findViewById(R.id.iv_bus_seat5)
        buszetels.add(busSeat5!!)

        busSeat6 = view.findViewById(R.id.iv_bus_seat6)
        buszetels.add(busSeat6!!)

        busSeat7 = view.findViewById(R.id.iv_bus_seat7)
        buszetels.add(busSeat7!!)

        busSeat8 = view.findViewById(R.id.iv_bus_seat8)
        buszetels.add(busSeat8!!)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        gebruiker = (activity as MainActivity).gebruiker
        txtHuidigeSessie!!.text = "Sessie " + gebruiker!!.sessieId.toString()

        for(i in 0..gebruiker!!.sessieId-1) {
            buszetels.get(i).visibility = ImageView.VISIBLE
        }

    }
}