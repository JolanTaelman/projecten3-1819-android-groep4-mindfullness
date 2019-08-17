package com.groep4.mindfulness.fragments

import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.groep4.mindfulness.R
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Oefening
import com.groep4.mindfulness.model.Sessie
import java.net.URL
import okhttp3.*


class FragmentOefeningFeedback : Fragment() {
    private var txtOefeningNaam : TextView? = null
    private var buttonOpslaan : Button? = null
    private var ratingBar : RatingBar? = null
    private var ratingFeedback : Int = 5
    private var txtFeedback : TextInputEditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        val view = inflater.inflate(R.layout.fragment_oefening_feedback, container, false)

        txtOefeningNaam = view.findViewById(R.id.textViewNaam) as TextView
        buttonOpslaan = view.findViewById(R.id.buttonBevestigen)
        ratingBar = view.findViewById(R.id.ratingBar) as RatingBar
        txtFeedback = view.findViewById(R.id.txtFeedback)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var oefening = arguments!!.getParcelable<Oefening>("oefening")
        val mainActivity = activity as MainActivity

        buttonOpslaan!!.setOnClickListener {
            val serverURL: String = "http://141.134.155.219:3000/oefeningen/oef/" + oefening.oefenigenId + "/feedback"
            val url = URL(serverURL)

                val builder = Request.Builder().url(url)

                val sb = StringBuilder()
                sb.append("oefeningId=" + oefening.oefenigenId)
                        .append("&&")
                        .append("beschrijving="+txtFeedback!!.text)
                        .append("&&")
                        .append("score=" + ratingFeedback)

                val fromBodyBuilder = FormBody.Builder()
                fromBodyBuilder.add("oefeningId", oefening.oefenigenId.toString())
                fromBodyBuilder.add("beschrijving", txtFeedback!!.text.toString())
                fromBodyBuilder.add("score", ratingFeedback.toString())

                var response = (activity as MainActivity)
                        .postFeedback("http://141.134.155.219:3000/oefeningen/oef/" + oefening.oefenigenId + "/feedback", fromBodyBuilder.build())


                txtOefeningNaam!!.text = response
            (activity as MainActivity).onBackPressed()
            }


            ratingBar!!.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                ratingFeedback = (rating * 2).toInt()
            }
        txtOefeningNaam!!.text = oefening.naam
    }


}