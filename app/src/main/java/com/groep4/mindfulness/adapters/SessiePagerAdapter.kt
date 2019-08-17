package com.groep4.mindfulness.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.groep4.mindfulness.fragments.FragmentSessiePageInfo
import com.groep4.mindfulness.fragments.FragmentSessiePageOefeningen
import com.groep4.mindfulness.model.Sessie

class SessiePagerAdapter(fm: FragmentManager, sessie: Sessie, page: Int) : FragmentStatePagerAdapter(fm) {

    var currentSessie: Sessie? = null
    private val bundle = Bundle()
    var currentPage: Int

    init {
        currentSessie = sessie
        currentPage = page
        bundle.putParcelable("key_sessie", currentSessie)
        bundle.putInt("key_page", currentPage)
    }

    /**
     * Geeft fragment terug adhv positie van de adapter
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragmentInfo = FragmentSessiePageInfo.newInstance()
                fragmentInfo.arguments = bundle
                fragmentInfo
            }
            1 -> {
                val fragmentExercises = FragmentSessiePageOefeningen.newInstance()
                fragmentExercises.arguments = bundle
                fragmentExercises
            }
            else -> {
                val fragmentInfo = FragmentSessiePageInfo.newInstance()
                fragmentInfo.arguments = bundle
                return fragmentInfo
            }
        }
    }

    /**
     * Geeft aantal fragments terug
     */
    override fun getCount(): Int {
        return 2
    }

    /**
     * Geeft paginatitel terug
     */
    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Info"
            1 -> "Oefeningen"
            else -> {
                return "Info"
            }
        }
    }
}