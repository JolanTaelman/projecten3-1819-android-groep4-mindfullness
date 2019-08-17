package com.groep4.mindfulness.fragments

import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.groep4.mindfulness.R
import com.groep4.mindfulness.utils.NotificationUtils
import kotlinx.android.synthetic.main.fragment_reminder.view.*
import java.text.DateFormat
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.receivers.AlarmReceiver
import es.dmoral.toasty.Toasty


class FragmentReminder : Fragment() {
    private var mNotified = false

    private var mTimeSetListener: TimePickerDialog.OnTimeSetListener? = null
    private var mDisplayTime: TextView? = null

    private var prefs: SharedPreferences? = null

    val PREFS_REMINDER = "com.groep4.mindfulness.prefs"
    val REMINDER_TIME = "reminder_time"
    val REMINDER_SET = "reminder_set"

    companion object {
        fun newInstance(): FragmentReminder {
            return FragmentReminder()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_reminder, container, false)

        view.tr_page.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPink))
        view.tv_page.setText(R.string.reminder)


        prefs = context?.getSharedPreferences(PREFS_REMINDER, 0)

        // Reminder enabled/disabled uit sharedpreferences halen
        val storedSet = prefs!!.getBoolean(REMINDER_SET, false)
        view.sw_reminder.isChecked = storedSet

        // Time uit sharedpreferences halen en invoegen in timepicker textview
        val storedTime = prefs!!.getLong(REMINDER_TIME, System.currentTimeMillis())
        mDisplayTime = view.findViewById(R.id.tv_reminder_timepicker)
        val content = SpannableString(DateFormat.getTimeInstance(DateFormat.SHORT).format(storedTime))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        mDisplayTime!!.text = content

        mDisplayTime!!.setOnClickListener {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            val dialog = TimePickerDialog(
                    activity,
                    R.style.MyTimePickerDialogStyle,
                    mTimeSetListener,
                    hour,
                    minute,
                    true
            )
            dialog.show()
        }

        mTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute -> mDisplayTime!!.text = String.format("%02d", hour) + " : " + String.format("%02d", minute)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val millis = calendar.timeInMillis
            setReminderNotification(millis)

            val editor = prefs!!.edit()
            editor.putLong(REMINDER_TIME, millis)
            editor.apply()

            setSwitchChecked(true)
            view.sw_reminder.isChecked = true
        }

        // Checked listener switch button
        view.sw_reminder.setOnCheckedChangeListener { _, isChecked ->
            setSwitchChecked(isChecked)
        }
        return view
    }

    private fun setSwitchChecked(isChecked: Boolean) {
        if (isChecked) {
            Toasty.success(context!!, "Dagelijkse notificatie aangezet!", Toast.LENGTH_SHORT, true).show()
            val editor = prefs!!.edit()
            editor.putBoolean(REMINDER_SET, true)
            editor.apply()
            setReminderNotification(prefs!!.getLong(REMINDER_TIME, System.currentTimeMillis()))
        } else {
            Toasty.success(context!!, "Dagelijkse notificatie uitgezet.", Toast.LENGTH_SHORT, true).show()
            val editor = prefs!!.edit()
            editor.putBoolean(REMINDER_SET, false)
            editor.apply()
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun setReminderNotification(time: Long) {
        // Indien tijd in het verleden ligt, voeg er 1 dag aan toe.
        // (nodig anders wordt notificatie direct na het setten al uitgevoerd)
        var fixedTime = time
        val diff = Calendar.getInstance().timeInMillis - time
        if (diff > 0) {
            fixedTime += 86400000
        }

        // Aanmaken van notificatie
        if (!mNotified) {
            NotificationUtils().setNotification(fixedTime, activity!!)
        } else {
            Toast.makeText(activity, "Already notified", Toast.LENGTH_SHORT).show()
        }
    }

}