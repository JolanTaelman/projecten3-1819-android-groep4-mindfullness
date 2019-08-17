package com.groep4.mindfulness.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import com.groep4.mindfulness.fragments.FragmentOefening
import com.groep4.mindfulness.fragments.FragmentSessie
import com.groep4.mindfulness.model.Oefening
import com.groep4.mindfulness.model.Sessie


internal class OefeningenPagerAdapter(fm: FragmentManager, val oefeningen: ArrayList<Oefening>) : FragmentPagerAdapter(fm) {

    var registeredFragments: SparseArray<Fragment> = SparseArray()


    /**
     * Aantal oefeningen
     */
    override fun getCount(): Int {
        return oefeningen.size
    }

    /**
     * Geeft een Oefeningfragment terug
     */
    override fun getItem(position: Int): Fragment {
        return FragmentOefening.newInstance(position + 1, position == count - 1, oefeningen[position])
    }

    /**
     * Geeft paginatitel
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }

    /**
     * Destroyt huidig fragment
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    /**
     * Instantieer fragment
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    /**
     * Geef huidig fragment
     */
    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }
}