package com.example.asyncsampleapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.app.DatePickerDialog
import java.util.*


class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() {view, year, month, dayOfMonth->
                var monthStr = (month + 1).toString()
                if (month < 10)
                    monthStr = "0" + (month + 1).toString()

                var dayStr = dayOfMonth.toString()
                if (dayOfMonth < 10)
                    dayStr = "0" + dayStr.toString()

                val intent = Intent()
                intent.putExtra("DATE", (year.toString() + "-" + monthStr + "-" + dayStr))
                setResult(RESULT_OK, intent)
                finish();
            },
            year,
            month,
            day)
        datePickerDialog.show()
    }
}