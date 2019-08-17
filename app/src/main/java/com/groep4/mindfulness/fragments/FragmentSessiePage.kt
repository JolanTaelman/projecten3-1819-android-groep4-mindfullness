package com.groep4.mindfulness.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groep4.mindfulness.R
import com.groep4.mindfulness.adapters.SessiePagerAdapter
import com.groep4.mindfulness.model.Sessie
import kotlinx.android.synthetic.main.fragment_sessie_page.view.*

class FragmentSessiePage : Fragment() {

    lateinit var sessie: Sessie
    var page: Int = 0

    companion object {
        fun newInstance(): FragmentSessiePage {
            return FragmentSessiePage()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sessie_page, container, false)

        val bundle = this.arguments

        if (bundle != null) {
            sessie = bundle.getParcelable("key_sessie")
            page = bundle.getInt("key_page")

            view.tv_page.text = sessie.naam
            view.tv_page.text = "Sessie $page"
        }

        val fragmentAdapter = SessiePagerAdapter(activity!!.supportFragmentManager, sessie, page)
        view.sessie_viewpager.adapter = fragmentAdapter
        view.sessie_tabs.setupWithViewPager(view.sessie_viewpager)

        return view
    }
}