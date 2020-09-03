package com.example.timemanager

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global.putString
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    fun updateChronoText(chrono : TextView , startTime : List<Int>, save : Boolean){
        // Get current time and format for display
        val current = LocalDateTime.now()
        val formatterH = DateTimeFormatter.ofPattern("HH")
        val formatterM = DateTimeFormatter.ofPattern("mm")
        val formatterS = DateTimeFormatter.ofPattern("ss")
        var hours = current.format(formatterH).toInt() - startTime.elementAt(0)
        var mins = current.format(formatterM).toInt() - startTime.elementAt(1)
        var seconds = current.format(formatterS).toInt() - startTime.elementAt(2)

        //correct for non-zero start time leading to negative numbers
        if (seconds < 0) {
            mins = mins - 1
            seconds = seconds + 60
        }
        if (mins < 0) {
            hours = hours - 1
            mins = mins + 60
        }

        if (save) {
            //saveData()
        }

        chrono.text = "$hours : $mins : $seconds"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val currentActivityText = findViewById<TextView>(R.id.activityText)
        val spinner : Spinner = findViewById(R.id.activitySpinner)
        val chronometer = findViewById<TextView>(R.id.chronometer)
        val durationsList = findViewById<TextView>(R.id.durationsList)

        //durationsList.text = R.array.activities


        val current = LocalDateTime.now()
        val formatterH = DateTimeFormatter.ofPattern("HH")
        val formatterM = DateTimeFormatter.ofPattern("mm")
        val formatterS = DateTimeFormatter.ofPattern("ss")
        val hours = current.format(formatterH).toInt()
        val mins = current.format(formatterM).toInt()
        val seconds = current.format(formatterS).toInt()


        var startTime = listOf<Int>(hours,mins,seconds)


        ArrayAdapter.createFromResource(
            this,
            R.array.activities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val chronometer = findViewById<TextView>(R.id.chronometer)

                //Reset start time
                val current = LocalDateTime.now()
                val formatterH = DateTimeFormatter.ofPattern("HH")
                val formatterM = DateTimeFormatter.ofPattern("mm")
                val formatterS = DateTimeFormatter.ofPattern("ss")
                val hours = current.format(formatterH).toInt()
                val mins = current.format(formatterM).toInt()
                val seconds = current.format(formatterS).toInt()

                startTime = listOf<Int>(hours,mins,seconds)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                updateChronoText(chronometer, startTime, false)
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun saveData() {
        val insertedText = "Test" //spinner.text.toString()

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("STRING_KEY", insertedText)
        }.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedString = sharedPreferences.getString("STRING_KEY", null)

        //spinner.text = savedString
    }
}
