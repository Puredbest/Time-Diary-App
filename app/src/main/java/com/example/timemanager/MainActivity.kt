package com.example.timemanager

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Insets.add
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

    fun updateChronoText(chrono: TextView , startTime: List<Int>, save: Boolean, map: MutableMap<String, Int>, selected: String){
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
            val durationsList = findViewById<TextView>(R.id.durationsList)

            var totalSeconds = hours*3600 + mins*60 + seconds
            map[selected] = map[selected]!! + totalSeconds
            saveData(map)

            val mapAsString = StringBuilder("")
            for (key in map.keys) {
                var dispS = map[key]
                var dispM = 0
                var dispH = 0

                if(dispS!! > 60){
                    dispM = dispS / 60
                    dispS = dispS!! % 60
                }
                if(dispM!! > 60){
                    dispH = dispM / 60
                    dispM = dispM!! % 60
                }
                mapAsString.append(key + "   -   " + "$dispH : $dispM : $dispS" + "\n")
            }
            mapAsString.delete(mapAsString.length - 1, mapAsString.length).append("")
            val displayString = mapAsString.toString()

            durationsList.text = displayString

        }

        chrono.text = "$hours : $mins : $seconds"
    }

    fun updateSpinner(map : MutableMap<String, Int>) {
        val spinner : Spinner = findViewById(R.id.activitySpinner)

        var activityNames : MutableList<String> = ArrayList()

        for ((k,v) in map){
            activityNames.add(k)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            activityNames
        )

        spinner.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner : Spinner = findViewById(R.id.activitySpinner)
        val chronometer = findViewById<TextView>(R.id.chronometer)
        val durationsList = findViewById<TextView>(R.id.durationsList)

//        var map = mutableMapOf<String, Int>(
//            "Netflix" to 0, "Work" to 0 , "Youtube" to 0, "Coding" to 0
//        )
//
//        saveData(map)
        var map = loadData()

        //updateSpinner(map)


        val current = LocalDateTime.now()
        val formatterH = DateTimeFormatter.ofPattern("HH")
        val formatterM = DateTimeFormatter.ofPattern("mm")
        val formatterS = DateTimeFormatter.ofPattern("ss")
        val hours = current.format(formatterH).toInt()
        val mins = current.format(formatterM).toInt()
        val seconds = current.format(formatterS).toInt()


        var startTime = listOf<Int>(hours,mins,seconds)

        var selected = "Netflix"


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val chronometer = findViewById<TextView>(R.id.chronometer)
                updateChronoText(chronometer, startTime, true, map, selected)

                selected = parent.getItemAtPosition(position).toString()

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
                updateChronoText(chronometer, startTime, false, map, selected)
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun saveData(map : Map<String, Int>) {
        //convert Map to String for storage in preferences
        val mapAsString = StringBuilder("")
        for (key in map.keys) {
            mapAsString.append(key + "=" + map[key] + ",")
        }
        mapAsString.delete(mapAsString.length - 1, mapAsString.length).append("")
        val storableString = mapAsString.toString()


        //Store in sharedPreferences
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("activities", storableString)
        }.apply()
    }

    private fun loadData(): MutableMap<String, Int> {
        val spinner : Spinner = findViewById(R.id.activitySpinner)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedString = sharedPreferences.getString("activities", null).toString()

        val map = convertString2Map(savedString).toMutableMap()

        updateSpinner(map)
        return map
    }

    private fun convertString2Map(mapAsString: String): Map<String, Int> {
        return mapAsString.split(",")
            .map { it.split("=") }
            .map { it.first() to it.last().toInt() }
            .toMap()
    }
}
