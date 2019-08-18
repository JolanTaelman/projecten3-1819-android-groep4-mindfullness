package com.groep4.mindfulness.fragments

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.*
import com.groep4.mindfulness.R
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Gebruiker
import com.groep4.mindfulness.model.Sessie
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_sessie.*
import kotlinx.android.synthetic.main.fragment_sessie.view.*
import java.util.*
import kotlin.collections.ArrayList


class FragmentSessie : Fragment() {

    private var callback: CallbackInterface? = null

    private var imgSessie: ImageView? = null
    private var cvSessie: CardView? = null
    private var txtSessieTitel: TextView? = null
    private var txtSessieBeschrijving: TextView? = null
    private var imgBusMonsters: ArrayList<ImageView>? = null
    private var imgViewsBuildings: ArrayList<ImageView>? = null
    private var imgViewsMisc: ArrayList<ImageView>? = null
    private var imgViewsMiscfg: ArrayList<ImageView>? = null
    private var imgViewsFw: ArrayList<ImageView>? = null
    private var imgFinish: ImageView? = null
    private val handler = Handler()
    private var gebruiker : Gebruiker? = Gebruiker()
    private var objectAnimator: ObjectAnimator? = null
    private var random: Random? = null
    private var screenWidth: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? CallbackInterface
        if (callback == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        random = Random()
        val view = inflater.inflate(R.layout.fragment_sessie, container, false)
        val main: MainActivity = (activity as MainActivity)
        cvSessie = view.findViewById(R.id.cv_sessie)
        imgSessie = view.findViewById(R.id.iv_sessie)
        txtSessieTitel = view.findViewById(R.id.tv_sessie)
        txtSessieBeschrijving = view.findViewById(R.id.tv_sessie_beschrijving)

        view.iv_bus.translationX = resources.displayMetrics.widthPixels.toFloat()
        view.iv_bus_gloss.translationX = resources.displayMetrics.widthPixels.toFloat()
        view.gl_bus_passengers.translationX = resources.displayMetrics.widthPixels.toFloat()

        // Bus-seats imageviews in array stoppen
        imgBusMonsters = ArrayList()
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat1))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat2))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat3))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat4))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat5))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat6))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat7))
        imgBusMonsters!!.add(view.findViewById(R.id.iv_bus_seat8))

        // Background buildings & misc imageviews in array stoppen & random weergeven
        imgViewsBuildings = ArrayList()
        imgViewsBuildings!!.add(view.findViewById(R.id.iv_sessie_building00))
        imgViewsBuildings!!.add(view.findViewById(R.id.iv_sessie_building01))

        imgViewsMisc = ArrayList()
        imgViewsMisc!!.add(view.findViewById(R.id.iv_sessie_miscbg00))
        imgViewsMisc!!.add(view.findViewById(R.id.iv_sessie_miscbg01))
        imgViewsMisc!!.add(view.findViewById(R.id.iv_sessie_miscbg02))
        imgViewsMisc!!.add(view.findViewById(R.id.iv_sessie_miscbg03))

        imgViewsMiscfg = ArrayList()
        imgViewsMiscfg!!.add(view.findViewById(R.id.iv_sessie_miscfg00))
        imgViewsMiscfg!!.add(view.findViewById(R.id.iv_sessie_miscfg01))
        imgViewsMiscfg!!.add(view.findViewById(R.id.iv_sessie_miscfg02))
        imgViewsMiscfg!!.add(view.findViewById(R.id.iv_sessie_miscfg03))
        imgViewsMiscfg!!.add(view.findViewById(R.id.iv_sessie_miscfg04))

        imgViewsFw = ArrayList()
        imgViewsFw!!.add(view.findViewById(R.id.iv_fw1))
        imgViewsFw!!.add(view.findViewById(R.id.iv_fw2))
        imgViewsFw!!.add(view.findViewById(R.id.iv_fw3))

        imgFinish = view.findViewById(R.id.iv_sessie_finish)

        // Dynamisch background images verdelen per sessie
        if (main.gebruiker!!.sessieId == 8){
        if (arguments!!.containsKey("isLast")){
            achtergrondFinish()
        } else {
            achtergrondRandomizen()
        }} else {
            achtergrondRandomizen()
        }
        screenWidth = resources.displayMetrics.widthPixels

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val page = arguments!!.getInt("page", 0)
        val sessie = arguments!!.getParcelable<Sessie>("sessie")

        val imgRes = context!!.resources.getIdentifier("mnstr$page","mipmap", context!!.packageName)
        imgSessie!!.setImageResource(imgRes)

        gebruiker = (activity as MainActivity)!!.gebruiker

        // Naargelang hoe ver je zit in de sessies de bus opvullen met monsters.
        for (i in 0 until page){
            if (i < 8){
                val imgResBus = context!!.resources.getIdentifier("mnstr" + (i+1),"mipmap", context!!.packageName)
                imgBusMonsters!![i].setImageResource(imgResBus)
                imgBusMonsters!![i].visibility = VISIBLE}
            }

        txtSessieTitel!!.text = sessie.naam

        //Open een sessie indien erop geklikt is
        cv_sessie.setOnClickListener{
            if (sessie!!.naam != "Geen sessie gevonden."){
                if (sessie!!.sessieId == 1){

                    //Creeer nieuwe fragment
                    val sessiePageFragment = FragmentSessiePage()
                    val bundle = Bundle()
                    bundle.putParcelable("key_sessie", sessie)
                    bundle.putInt("key_page", page)
                    sessiePageFragment.arguments = bundle

                    // Launch fragment met callback naar activity
                    callback?.setFragment(sessiePageFragment, true)
                }
                else if(gebruiker!!.sessieId+1 > sessie!!.sessieId) {
                    //creeer nieuwe fragment
                    val sessiePageFragment = FragmentSessiePage()
                    val bundle = Bundle()
                    bundle.putParcelable("key_sessie", sessie)
                    bundle.putInt("key_page", page)
                    sessiePageFragment.arguments = bundle

                    // Launch fragment met callback naar activity
                    callback?.setFragment(sessiePageFragment, true)
                } else if(gebruiker!!.sessieId+1 < sessie!!.sessieId)
                    Toast.makeText(context, "De sessie is nog niet toegankelijk", Toast.LENGTH_SHORT).show()
                else {

                    val builder = AlertDialog.Builder(context!!)
                    var editTextCode: EditText

                    builder.setTitle("Sessie gelocked!")
                    builder.setMessage("Gelieve de sessiecode in te geven")
                    builder.setCancelable(true)

                    editTextCode = EditText(context)
                    editTextCode.hint = "Sessiecode"
                    editTextCode.inputType = InputType.TYPE_CLASS_TEXT

                    builder.setPositiveButton("Stuur") { dialog, which ->
                        var code = editTextCode.text.toString()

                        if (sessie!!.sessieCode == code) {

                            //creeer nieuwe fragment
                            val sessiePageFragment = FragmentSessiePage()
                            val bundle = Bundle()
                            bundle.putParcelable("key_sessie", sessie)
                            bundle.putInt("key_page", page)
                            sessiePageFragment.arguments = bundle
                            (activity as MainActivity)!!.sessieUnlocked()

                            // Launch fragment met callback naar activity
                            callback?.setFragment(sessiePageFragment, true)
                        } else {
                            Toast.makeText(context, "Helaas, het antwoord is fout", Toast.LENGTH_SHORT).show()

                        }
                    }

                    builder.setNeutralButton("Annuleer") { dialog, which ->
                        dialog.cancel()
                    }

                    val dialog: AlertDialog = builder.create()

                    dialog.setView(editTextCode)
                    dialog.show()
                }
            }else{
                Toasty.info(view!!.context, "Geen sessie gevonden, controleer uw internetverbinding.").show()
            }
        }
    }

    /*
    Hierin wordt de bus zichtbaar gemaakt
     */
    fun setBusVisible(isVisible: Boolean){
        val root = view!!.findViewById(R.id.rl_sessie) as RelativeLayout
        if(isVisible){
            root.iv_bus.visibility = VISIBLE
            root.iv_bus_gloss.visibility = VISIBLE
            root.gl_bus_passengers.visibility = VISIBLE
        }else{
            root.iv_bus.visibility = INVISIBLE
            root.iv_bus_gloss.visibility = INVISIBLE
            root.gl_bus_passengers.visibility = INVISIBLE
        }
    }

    /*
    In deze methode wordt er met de bus gereden tot in het midden van de frame,
    de rijrichting hangt af vvan in welke richting geswiped
     */
    fun drive(forward: Boolean){
        val root = view!!.findViewById(R.id.rl_sessie) as RelativeLayout

        if(forward){
            handleDriveForward(root.iv_bus)
            handleDriveForward(root.iv_bus_gloss)
            handleDriveForward(root.gl_bus_passengers)
        }else{
            handleDriveBackward(root.iv_bus)
            handleDriveBackward(root.iv_bus_gloss)
            handleDriveBackward(root.gl_bus_passengers)
        }
        setBusVisible(true)
    }

    private fun handleDriveForward(v: View){
        objectAnimator = ObjectAnimator.ofFloat(v, "translationX", -screenWidth!!.toFloat(), 0f)
        objectAnimator!!.duration = 1100
        objectAnimator!!.start()
    }

    private fun handleDriveBackward(v: View){
        objectAnimator = ObjectAnimator.ofFloat(v, "translationX", screenWidth!!.toFloat(), 0f)
        objectAnimator!!.duration = 1100
        objectAnimator!!.start()
    }

    private fun achtergrondRandomizen(){
        for (v in imgViewsBuildings!!) {
            v.setImageResource((parentFragment as FragmentSessieLijst).imgBuildings!![random!!.nextInt((parentFragment as FragmentSessieLijst).imgBuildings!!.size)])
            when (random!!.nextBoolean()) {
                true -> v.visibility = VISIBLE
                false -> v.visibility = INVISIBLE
            }
        }

        for (v in imgViewsMisc!!) {
            v.setImageResource((parentFragment as FragmentSessieLijst).imgMisc!![random!!.nextInt((parentFragment as FragmentSessieLijst).imgMisc!!.size)])
        }

        for (v in imgViewsFw!!){
            v.visibility = INVISIBLE
        }
        imgFinish!!.visibility = INVISIBLE
    }

    /*
    Hierin wordt de achtergrond van de finish gemaakt
     */
    private fun achtergrondFinish(){
        imgFinish!!.visibility = VISIBLE

        cvSessie!!.visibility = INVISIBLE


        for (v in imgViewsBuildings!!){
            v.visibility = INVISIBLE
        }


        for (v in imgViewsMisc!!){
            v.visibility = INVISIBLE
        }

        for (v in imgViewsMiscfg!!){
            v.visibility = INVISIBLE
        }
        fireworkAnimationSmall()
    }

    /*
    Hierin wordt de animatie ingesteld
     */
    fun fireworkAnimationSmall(){
        for (v in imgViewsFw!!){
            v.scaleX = 0f
            v.scaleY = 0f
        }
    }

    fun fireworkAnimationBig(){
        objectAnimator = ObjectAnimator.ofFloat(imgViewsFw!![0], "scaleX", 1f)
        objectAnimator!!.duration = 1500
        objectAnimator!!.start()

        objectAnimator = ObjectAnimator.ofFloat(imgViewsFw!![0], "scaleY", 1f)
        objectAnimator!!.duration = 1500
        objectAnimator!!.start()

        objectAnimator = ObjectAnimator.ofFloat(imgViewsFw!![1], "scaleX", 1f)
        objectAnimator!!.duration = 1500
        objectAnimator!!.startDelay = 500
        objectAnimator!!.start()

        objectAnimator = ObjectAnimator.ofFloat(imgViewsFw!![1], "scaleY", 1f)
        objectAnimator!!.duration = 1500
        objectAnimator!!.startDelay = 500
        objectAnimator!!.start()

        objectAnimator = ObjectAnimator.ofFloat(imgViewsFw!![2], "scaleX", 1f)
        objectAnimator!!.duration = 1500
        objectAnimator!!.startDelay = 1000
        objectAnimator!!.start()

        objectAnimator = ObjectAnimator.ofFloat(imgViewsFw!![2], "scaleY", 1f)
        objectAnimator!!.duration = 1500
        objectAnimator!!.startDelay = 1000
        objectAnimator!!.start()
    }

    /*
    Hierin wordt een geluid van vuurwerk gemaakt en afgespeeld
     */
     fun playSound(){
        val mp1: MediaPlayer = MediaPlayer.create(context, R.raw.vuurwerk)
        mp1.isLooping = false

         val mp2: MediaPlayer = MediaPlayer.create(context, R.raw.vuurwerk)
         mp1.isLooping = false

         val mp3: MediaPlayer = MediaPlayer.create(context, R.raw.vuurwerk)
         mp1.isLooping = false

        mp1.start()

         handler.postDelayed({
             mp2.start()
         },500)

         handler.postDelayed({
             mp3.start()
         },1000)
    }

    companion object {
        fun newInstance(page: Int, isLast: Boolean, sessie: Sessie): FragmentSessie {
            val args = Bundle()
            args.putInt("page", page)
            if (isLast)
                args.putBoolean("isLast", true)
            args.putParcelable("sessie", sessie)
            val fragment = FragmentSessie()
            fragment.arguments = args
            return fragment
        }
    }
}