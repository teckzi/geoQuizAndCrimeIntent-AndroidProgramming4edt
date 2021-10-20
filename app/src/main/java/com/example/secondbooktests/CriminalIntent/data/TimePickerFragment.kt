package com.example.secondbooktests.CriminalIntent.data

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*


private const val ARG_TIME = "time"

class TimePickerFragment:DialogFragment() {
    interface Callbacks {
        fun onTimeSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_TIME) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val timeListener = TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            //time_Picker.text = java.text.SimpleDateFormat("HH:mm").format(calendar.time)
        }

        return TimePickerDialog(
            requireContext(),
            timeListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true)
    }
    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }

    }
}