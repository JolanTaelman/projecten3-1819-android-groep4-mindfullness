package com.groep4.mindfulness.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.job.JobScheduler
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.groep4.mindfulness.R
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.adapters.ListTaskAdapter
import com.groep4.mindfulness.utils.NoScrollListView
import com.groep4.mindfulness.database.DBHelper
import com.groep4.mindfulness.utils.KalenderFunction
import kotlinx.android.synthetic.main.fragment_kalender.view.*
import java.util.ArrayList
import java.util.HashMap
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Gebruiker
import com.groep4.mindfulness.model.Task
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_kalender.*


class FragmentKalender : Fragment()
{
    private var callback: CallbackInterface? = null

    lateinit var mydb: DBHelper
    lateinit var taskNoScrollListToday: NoScrollListView ; lateinit var taskNoScrollListTomorrow: NoScrollListView  ; lateinit var taskNoScrollListUpcoming: NoScrollListView
    lateinit var scrollView: NestedScrollView
    lateinit var todayText: TextView ; lateinit var tomorrowText: TextView ; lateinit var upcomingText: TextView
    lateinit var ref:DatabaseReference
    var todayList = ArrayList<HashMap<String, String>>()
    var tomorrowList = ArrayList<HashMap<String, String>>()
    var upcomingList = ArrayList<HashMap<String, String>>()
    private var gebruiker : Gebruiker? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? CallbackInterface
        if (callback == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view = inflater.inflate(R.layout.fragment_kalender, container, false)
        gebruiker = (activity as MainActivity).gebruiker

        // Top bar info instellen
        view.tr_page.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPurple))
        view.tv_page.setText(R.string.kalender)

        scrollView = view.findViewById(R.id.scrollView) as NestedScrollView

        taskNoScrollListToday = view.findViewById(R.id.taskListToday) as NoScrollListView
        taskNoScrollListTomorrow = view.findViewById(R.id.taskListTomorrow) as NoScrollListView
        taskNoScrollListUpcoming = view.findViewById(R.id.taskListUpcoming) as NoScrollListView

        todayText = view.findViewById(R.id.todayText) as TextView
        tomorrowText = view.findViewById(R.id.tomorrowText) as TextView
        upcomingText = view.findViewById(R.id.upcomingText) as TextView

        //Fragment knop taak toevoegen
        val addTaskButton = view.findViewById(R.id.addTaskButton) as ImageView
        addTaskButton.setOnClickListener {

            //Een nieuwe fragment aanmaken
            val f = FragmentAddTask()
            val bundle = Bundle()

            //isUpdate = false aangezien dit een nieuwe taak is en geen aanpassing
            bundle.putBoolean("isUpdate", false)
            f.arguments = bundle
            f.arguments = bundle


            // Launch fragment met callback naar activity
            callback?.setFragment(f, true)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        //database opstellen
        mydb = DBHelper(activity!!)
        populateData()

    }

    /**
     * Data ophalen
     */
    fun populateData() {
        mydb = DBHelper(activity!!)
        scrollView!!.visibility = View.GONE
        val loadTask = LoadTask()
        loadTask.execute()

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    /**
     *  asynchroon ophalen van alle tasks
     */
    inner class LoadTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

            todayList.clear()
            tomorrowList.clear()
            upcomingList.clear()


        }

        override fun doInBackground(vararg args: String): String {
            val xml = ""

            loadFirebaseDataList()

            Thread.sleep(1800)
            val today = mydb.dataToday
            loadSQLDataList(today, todayList)
            val tomorrow = mydb.dataTomorrow
            loadSQLDataList(tomorrow, tomorrowList)
            val upcoming = mydb.dataUpcoming
            loadSQLDataList(upcoming, upcomingList)

            return xml
        }

        override fun onPostExecute(xml: String) {

            /**
             *  ID , TASK , DATE ->> ophalen uit datebase
             *  Date naar leesbare string omzetten
             */


            loadListView(taskNoScrollListToday, todayList)
            loadListView(taskNoScrollListTomorrow, tomorrowList)
            loadListView(taskNoScrollListUpcoming, upcomingList)

            if (todayList.size > 0) {
                todayText.visibility = View.VISIBLE
            } else {
                todayText.visibility = View.GONE
            }

            if (tomorrowList.size > 0) {
                tomorrowText.visibility = View.VISIBLE
            } else {
                tomorrowText.visibility = View.GONE
            }

            if (upcomingList.size > 0) {
                upcomingText.visibility = View.VISIBLE
            } else {
                upcomingText.visibility = View.GONE
            }

            scrollView.visibility = View.VISIBLE
        }

        }






    /**
     *  ID , TASK , DATE ->> ophalen uit datebase
     *  Date naar leesbare string omzetten
     */


    fun loadSQLDataList(cursor: Cursor?, dataList: ArrayList<HashMap<String, String>>) {


        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {

                val mapToday = HashMap<String, String>()
                mapToday[KEY_ID] = cursor.getString(0).toString()
                mapToday[KEY_TASK] = cursor.getString(1).toString()
                mapToday[KEY_DATE] = KalenderFunction().Epoch2DateString(cursor.getString(2).toString(), "dd-MM-yyyy")
                dataList.add(mapToday)
                cursor.moveToNext()

            }
            cursor.close()
        }

    }
    fun loadFirebaseDataList(){
        ref = FirebaseDatabase.getInstance().getReference("Announcement")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot!!.exists()){
                    for (data in dataSnapshot.children){
                        val _task = data.getValue(Task::class.java)
                        try{
                            val cursordb:Cursor = mydb.getDataSpecific(_task!!._key)

                            if(cursordb.count <=0) {
                                if(_task._group == gebruiker!!.groepsnr.toString()){
                                mydb.insertTaskwithid(_task!!._key,_task!!._text,_task._date.toString())
                                }
                            }
                        }catch (e:Exception){
                            System.out.println(e.printStackTrace())
                        }
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    /**
     * Task update als er word op geklikt
     */
    fun loadListView(listView: ListView, dataList: ArrayList<HashMap<String, String>>) {

        val adapter = ListTaskAdapter(activity!!, dataList)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        var f = FragmentAddTask()
                        val bundle = Bundle()
                        bundle.putBoolean("isUpdate", true)
                        bundle.putString("id", dataList[+position][KEY_ID])
                        f.arguments = bundle
                        callback?.setFragment(f, true)
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {

                        mydb.deleteTask(dataList[+position][KEY_ID])
                        dataList.clear()
                        populateData()
                        adapter.notifyDataSetChanged()
                    }
                }

            }

            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Wat wil je doen ?").setPositiveButton("Aanpassen", dialogClickListener)
                    .setNegativeButton("Verwijderen", dialogClickListener).show()

            adapter.notifyDataSetChanged()

        }
    }




    companion object {

        var KEY_ID = "id"
        var KEY_TASK = "task"
        var KEY_DATE = "date"
    }
}