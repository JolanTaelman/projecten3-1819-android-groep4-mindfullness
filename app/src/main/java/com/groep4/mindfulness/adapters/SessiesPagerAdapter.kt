package com.groep4.mindfulness.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import com.groep4.mindfulness.fragments.FragmentSessie
import com.groep4.mindfulness.model.Sessie


internal class SessiesPagerAdapter(fm: FragmentManager, val sessies: ArrayList<Sessie>) : FragmentPagerAdapter(fm) {

    var registeredFragments: SparseArray<Fragment> = SparseArray()

    /**
    * Geeft aantal fragmenten terug
    */
    override fun getCount(): Int {
        return sessies.size
    }

    /**
     * Geeft fragment terug
     */
    override fun getItem(position: Int): Fragment {
        return FragmentSessie.newInstance(position + 1, position == count - 1, sessies[position])
    }
    /**
     * Geeft fragmenttitel terug
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }
    /**
     * Destroy fragment
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
     * Geeft huidig fragment terug
     */
    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }
}