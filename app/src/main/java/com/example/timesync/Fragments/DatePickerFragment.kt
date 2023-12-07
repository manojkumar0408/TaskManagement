package com.example.timesync.Fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class DatePickerFragment : DialogFragment() {

    interface OnDateSetListener {
        fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
    }

    private var onDateSetListener: OnDateSetListener? = null

    fun setOnDateSetListener(listener: OnDateSetListener) {
        this.onDateSetListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]

        return DatePickerDialog(requireContext(), dateSetListener, year, month, day)
    }

    private val dateSetListener =
        OnDateSetListener { _, year, month, dayOfMonth ->
            onDateSetListener?.onDateSet(year, month, dayOfMonth)
        }
}
