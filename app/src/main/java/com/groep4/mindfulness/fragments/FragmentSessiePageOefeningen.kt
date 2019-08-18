package com.groep4.mindfulness.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.badoualy.stepperindicator.StepperIndicator
import com.groep4.mindfulness.R
import com.groep4.mindfulness.adapters.OefeningenPagerAdapter
import com.groep4.mindfulness.model.Oefening
import com.groep4.mindfulness.model.Sessie
import java.util.*


class FragmentSessiePageOefeningen : Fragment() {

    lateinit var sessie: Sessie
    private lateinit var oefeningen: ArrayList<Oefening>
    private var txtGeenOefeningen : TextView? = null
    lateinit var mp: MediaPlayer

    companion object {
        fun newInstance(): FragmentSessiePageOefeningen {
            return FragmentSessiePageOefeningen()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sessie_page_oefeningen, container, false)
        //expandingList = view.findViewById(R.id.expanding_list_main)

        txtGeenOefeningen = view.findViewById(R.id.txtGeenOefeningen)

        oefeningen = arrayListOf()

        val bundle = this.arguments
        if (bundle != null) {
            sessie = bundle.getParcelable("key_sessie")
        }

        // Oefening toevoegen
        addOefeningen()

        val pager = view.findViewById<ViewPager>(R.id.pager_oefeningen)!!
        val stepper = view.findViewById<StepperIndicator>(R.id.si_oefeningen)
        val pagerAdapter = OefeningenPagerAdapter(childFragmentManager, oefeningen)
        pager.adapter = pagerAdapter

        if(!oefeningen.isEmpty()) {
            // offscreenpagelimit nodig zodat de pages niet telkens herladen worden bij het scrollen
            pager.offscreenPageLimit = oefeningen.size


            stepper.setViewPager(pager, (pager.adapter as OefeningenPagerAdapter).count)

            // Audioplayer
            mp = MediaPlayer.create(context, R.raw.ademmeditatie)
            mp.isLooping = false
            txtGeenOefeningen!!.visibility = View.GONE
        } else {
            pager.offscreenPageLimit = 1
            stepper.setViewPager(pager, 1)
            txtGeenOefeningen!!.visibility = View.VISIBLE
        }

        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                var pos: Int = 0

                if (position != 0){
                    pos = position - 1
                }

                val oefFragment : FragmentOefening = pagerAdapter.getRegisteredFragment(pos) as FragmentOefening
                if (oefFragment.mp.isPlaying) {
                    oefFragment.mp.pause()
                    oefFragment.mp.stop()
                    oefFragment.mp.release()
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })



        return view
    }

    /*
    Hierin worden alle oefeningen toegevoegd
     */
    private fun addOefeningen() {
        oefeningen.clear()

        // icons toevoegen
        val icons: ArrayList<Int> = ArrayList()
        icons.add(R.mipmap.lotusposition1)
        icons.add(R.mipmap.lotusposition2)
        icons.add(R.mipmap.lotusposition3)
        icons.add(R.mipmap.lotusposition4)
        icons.add(R.mipmap.lotusposition5)
        icons.add(R.mipmap.lotusposition6)
        icons.add(R.mipmap.lotusposition7)
        icons.add(R.mipmap.lotusposition8)

        if (sessie.oefeningen != null){
            oefeningen.addAll(sessie.oefeningen!!)
        }
    }
}