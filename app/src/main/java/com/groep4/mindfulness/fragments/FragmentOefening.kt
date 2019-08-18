package com.groep4.mindfulness.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import com.groep4.mindfulness.R
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.model.Oefening
import com.koushikdutta.ion.Ion
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_oefening.*


class FragmentOefening : Fragment() {

    private var callback: CallbackInterface? = null

    private var txtOefeningNaam: TextView? = null
    private var txtOefeningBeschrijving: TextView? = null
    private var ibAudio: ImageButton? = null
    private var wvPDF: WebView? = null
    private var ivOefening: ImageView? = null
    private var buttonFeedback : Button? = null

    private var isPlaying: Boolean = false
    val mp = MediaPlayer()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? CallbackInterface
        if (callback == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_oefening, container, false)

        txtOefeningNaam = view.findViewById(R.id.tv_oefening_naam)
        txtOefeningBeschrijving = view.findViewById(R.id.tv_oefening_beschrijving)
        ibAudio = view.findViewById(R.id.ib_playAudio)
        wvPDF = view.findViewById(R.id.wv_pdf)
        ivOefening = view.findViewById(R.id.iv_oefening)
        buttonFeedback = view.findViewById(R.id.buttonFeedback)

        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val page = arguments!!.getInt("page", 0)
        val oefening = arguments!!.getParcelable<Oefening>("oefening")


        txtOefeningNaam!!.text = oefening!!.naam
        txtOefeningBeschrijving!!.text = oefening.beschrijving
        txtOefeningBeschrijving!!.movementMethod = ScrollingMovementMethod()

        // Toont pdf als oefening pdfbestand is
        if (oefening.fileMimeType == "application/pdf"){
            wvPDF!!.visibility = View.VISIBLE
            wvPDF!!.settings.javaScriptEnabled = true
           val url = "https://docs.google.com/gview?embedded=true&url=" + oefening.fileUrl
           wvPDF!!.loadUrl(url)

        }

        // Toont afbeelding als oefening jpgbestand is
        if (oefening.fileMimeType == "image/jpeg"){
            ivOefening!!.visibility = View.VISIBLE


            Ion.with(ivOefening)
                    .load( oefening.fileUrl)

            ivOefening!!.setOnClickListener{
                val i = Intent(android.content.Intent.ACTION_VIEW)
                i.setDataAndType(Uri.parse(oefening.fileUrl), "image/jpg")
                startActivity(i)
            }
        }

        // Toont afspeelknop als oefening audiobestand is
        if (oefening.fileMimeType.startsWith("audio")){
            mp.setDataSource(oefening.fileUrl)


            ibAudio!!.visibility = View.VISIBLE
            ibAudio!!.setOnClickListener {
                isPlaying = if (!isPlaying){
                    ibAudio!!.setImageResource(R.drawable.ic_pause_white_24dp)
                    mp.prepare()
                    //mp.prepareAsync()
                    mp.start()
                    mp.setOnCompletionListener {
                        ibAudio!!.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        mp.stop()
                        isPlaying = false
                    }
                    true
                }else{
                    ibAudio!!.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    mp.stop()
                    false
                }
            }
        }

        // Toont video als oefening videobestand is
        if (oefening.fileMimeType.startsWith("video")){

            var controller = MediaController(context)
            controller.setMediaPlayer(videoView)
            videoView!!.setMediaController(controller)
            videoView!!.visibility = View.VISIBLE
            videoView!!.setVideoPath(oefening.fileUrl)
            videoView!!.setOnPreparedListener {
                controller.setAnchorView(videoView)
            }
        }

        //Toon de feedbackfragment indien of de feedbackknop geklikt is
        buttonFeedback!!.setOnClickListener{
            if (oefening!!.naam != "Geen oefening gevonden."){

                //Creeer nieuwe fragment
                val oefeningFeedbackFragment = FragmentOefeningFeedback()
                val bundle = Bundle()
                bundle.putParcelable("oefening", oefening)
                bundle.putInt("key_page", page)
                oefeningFeedbackFragment.arguments = bundle

                // Launch fragment met callback naar activity
                callback?.setFragment(oefeningFeedbackFragment, false)
            }else{
                Toasty.info(view!!.context, "Geen oefening gevonden, controleer uw internetverbinding.").show()
            }
        }
    }

    override fun onStop() {
        mp.stop()
        mp.release()
        super.onStop()
    }



    companion object {
        fun newInstance(page: Int, isLast: Boolean, oefening: Oefening): FragmentOefening {
            val args = Bundle()
            args.putInt("page", page)
            if (isLast)
                args.putBoolean("isLast", true)
            args.putParcelable("oefening", oefening)
            val fragment = FragmentOefening()
            fragment.arguments = args
            return fragment
        }
    }
}