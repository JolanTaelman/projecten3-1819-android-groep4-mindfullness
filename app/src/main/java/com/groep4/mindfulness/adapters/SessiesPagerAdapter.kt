package com.groep4.mindfulness.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import com.groep4.mindfulness.fragments.FragmentSessie
import com.groep4.mindfulness.model.Sessie


internal class SessiesPagerAdapter(fm: FragmentManager, val sessies: ArrayList<Sessie>) : FragmentPagerAdapter(fm) {

    var registeredFragments: SparseArray<Fragment> = SparseArray()

    override fun getCount(): Int {
        return sessies.size
    }

    override fun getItem(position: Int): Fragment {
//        return FragmentSessie.newInstance(position + 1, position == count - 1, sessies[position])
        return FragmentSessie.newInstance(position + 1, position == count - 1, sessies[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }
}