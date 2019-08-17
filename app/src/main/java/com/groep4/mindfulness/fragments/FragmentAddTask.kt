package com.groep4.mindfulness.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.groep4.mindfulness.R
import com.groep4.mindfulness.database.DBHelper
import com.groep4.mindfulness.utils.KalenderFunction
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*

class FragmentAddTask : Fragment(), com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {


    lateinit var mydb: DBHelper
    lateinit var dpd: com.wdullaer.materialdatetimepicker.date.DatePickerDialog
    var startYear = 0; var startMonth = 0 ;var startDay = 0
    var dateFinal: String = ""
    var nameFinal: String = ""
    var isUpdate: Boolean = false
    var id: String = ""
    var yearNow = 0
    var monthNow = 0
    var dayNow = 0


    lateinit var toolbar_task_add_title : TextView
    lateinit var task_name : TextView
    lateinit var task_date : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view:View=inflater!!.inflate(R.layout.fragment_kalender_add,container,false)

        // Als er een Task moet worden bijgewerkt word dit true
        isUpdate = arguments!!.getBoolean("isUpdate", false)

        //Calendar instance
        dateFinal = todayDateString()
        val your_date = Date()
        val cal = Calendar.getInstance()
        cal.setTime(your_date)
        startYear = cal.get(Calendar.YEAR)
        startMonth = cal.get(Calendar.MONTH)
        startDay = cal.get(Calendar.DAY_OF_MONTH)
        yearNow = cal.get(Calendar.YEAR)
        monthNow = cal.get(Calendar.MONTH)
        dayNow = cal.get(Calendar.DAY_OF_MONTH)

        toolbar_task_add_title = view.findViewById(R.id.toolbar_task_add_title) as TextView
        task_name = view.findViewById(R.id.task_name) as TextView
        task_date = view.findViewById(R.id.task_date) as TextView

        val closeButton = view.findViewById(R.id.closeAddTask) as ImageView
        closeButton.setOnClickListener {
            activity!!.onBackPressed()
        }

        val doneButton = view.findViewById(R.id.doneAddTask) as ImageView
        doneButton.setOnClickListener {
            doneAddTask(view!!)
        }

        val dateButton = view.findViewById(R.id.task_date) as TextView
        dateButton.setOnClickListener {
            showStartDatePicker(view!!)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        mydb = DBHelper(activity!!.applicationContext)

        if (isUpdate!!) {
            init_update()
        }
    }

    /**
     *  Word opgeroepen wanneer een Task moet worden bijgewerkt
     */
    fun init_update() {

        id = arguments!!.getString("id")

        //title veranderen toolbar
        toolbar_task_add_title.setText("Update")
        doAsync {
        val task = mydb.getDataSpecific(id)

        if (task != null) {
            task!!.moveToFirst()

            task_name.setText(task!!.getString(1).toString())
            val cal = KalenderFunction().Epoch2Calender(task!!.getString(2).toString())

            startYear = cal.get(Calendar.YEAR)
            startMonth = cal.get(Calendar.MONTH)
            startDay = cal.get(Calendar.DAY_OF_MONTH)

            task_date.setText(KalenderFunction().Epoch2DateString(task!!.getString(2).toString(), "dd/MM/yyyy"))
        }
        }

    }

    /**
     * Geeft datum van vandaag terug
     */
    fun todayDateString(): String {
        val dateFormat = SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault())

        return dateFormat.toString()

    }

    fun doneAddTask(v: View)
    {
        var errorStep = 0
        val task_name = view?.findViewById(R.id.task_name) as TextView
        val task_date = view?.findViewById(R.id.task_date) as TextView
        nameFinal = task_name.getText().toString()
        dateFinal = task_date.getText().toString()


        /**
         *  Validatie input
         */

        if (nameFinal.trim { it <= ' ' }.length < 1)
        {
            errorStep++
            task_name.setError("Geef een taaknaam op.")
        }
        if (dateFinal.trim { it <= ' ' }.length < 4)
        {
            errorStep++
            task_date.setError("Geef een specifieke datum op.")
        } else
        {
            if (startYear < yearNow)
            {
                errorStep++
                Toast.makeText(activity?.applicationContext, "Datum mag niet in het verleden liggen", Toast.LENGTH_SHORT).show()
            } else if (startYear == yearNow && startMonth < monthNow)
            {
                errorStep++
                Toast.makeText(activity?.applicationContext, "Datum mag niet in het verleden liggen", Toast.LENGTH_SHORT).show()
            } else if (startYear == yearNow && startMonth == monthNow && startDay < dayNow)
            {
                errorStep++
                Toast.makeText(activity?.applicationContext, "Datum mag niet in het verleden liggen", Toast.LENGTH_SHORT).show()
            }
        }



        if (errorStep == 0)
        {
            if (isUpdate!!)
            {
                doAsync {
                mydb.updateTask(id, nameFinal, dateFinal)
                }
                Toast.makeText(activity?.applicationContext, "Taak bijgewerkt.", Toast.LENGTH_SHORT).show()
            } else
            {
                doAsync {
                    mydb.insertTask(nameFinal, dateFinal)
                }
                Toast.makeText(activity?.applicationContext, "Taak toegevoegd.", Toast.LENGTH_SHORT).show()

            }
            activity!!.onBackPressed()
        } else
        {


        }

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
     *  Datum tonen die je hebt gekozen
     */

    override fun onDateSet(view: com.wdullaer.materialdatetimepicker.date.DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        startYear = year
        startMonth = monthOfYear
        startDay = dayOfMonth
        val monthAddOne = startMonth + 1
        val date = (if (startDay < 10) "0$startDay" else "" + startDay) + "/" +
                (if (monthAddOne < 10) "0$monthAddOne" else "" + monthAddOne) + "/" +
                startYear
        val task_date = getView()?.findViewById(R.id.task_date) as TextView
        task_date.setText(date)
    }

    /**
     *  DatePickerDialog fragment openen
     */

    fun showStartDatePicker(v: View) {
        dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance { view, year, monthOfYear, dayOfMonth -> this@FragmentAddTask; startYear; startMonth; startDay }
        dpd!!.setOnDateSetListener(this)
        dpd!!.show(activity?.fragmentManager, "startDatepickerdialog")
    }


}