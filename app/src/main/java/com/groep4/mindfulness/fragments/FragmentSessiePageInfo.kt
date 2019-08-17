package com.groep4.mindfulness.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groep4.mindfulness.R
import com.groep4.mindfulness.model.Sessie
import kotlinx.android.synthetic.main.fragment_sessie_page_info.view.*

class FragmentSessiePageInfo : Fragment() {

    lateinit var sessie: Sessie

    companion object {
        fun newInstance(): FragmentSessiePageInfo {
            return FragmentSessiePageInfo()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sessie_page_info, container, false)

        val bundle = this.arguments

        if (bundle != null) {
            sessie = bundle.getParcelable("key_sessie")
            view.tv_sessie_naam.text = sessie.naam
            view.tv_sessie_beschrijving.text = sessie.beschrijving
            view.tv_sessie_beschrijving.movementMethod = ScrollingMovementMethod()

            var page = bundle.getInt("key_page")
            view.iv_sessie_monster.setImageResource(context!!.resources.getIdentifier("mnstr$page","mipmap", context!!.packageName))

        }
        return view
    }
}