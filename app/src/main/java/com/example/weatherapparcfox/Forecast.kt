package com.example.weatherapparcfox

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Forecast : AppCompatActivity() {

    //This Forecast Activity is used to show the future weather stats

    //API gained from OpenWeatherMap API
    val API: String = "3a720b3a3e177f56b462b62605099cdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        //button to return to MainActivity
        val button: Button = findViewById(R.id.toCurrent)

        button.setOnClickListener(){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


        supportActionBar?.hide()
        weatherDetails().execute()
    }


    inner class weatherDetails() : AsyncTask<String, Void, String>() {
        //checkDate Global var is used later to populate each cell

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDatevalidateFormat = sdf.format(Date())

        val currentDateValidate = currentDatevalidateFormat.toString()
        var checkDate:String? = currentDateValidate

        override fun onPreExecute() {
            super.onPreExecute()
            //show progress bar and hide main view whilst loading data
            findViewById<ProgressBar>(R.id.idloading).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.RelPar).visibility = View.GONE

        }

        override fun doInBackground(vararg p0: String?): String? {
            //load in data from Openweathermap forecasted weather API
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/forecast?id=2655984&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var index: Int = 0
            var count: Int = 0

            //load elements required from JSON, while loop ensures that each cell gets its own data
            //which will in turn be used for each cell, at count 3 the final 4th cell is filled therefore ending the
            //while loop
            while (count < 4) {
                try {
                    val jsonObj = JSONObject(result)
                    val list = jsonObj.getJSONArray("list").getJSONObject(index)
                    val main = list.getJSONObject("main")
                    val wind = list.getJSONObject("wind")

                    var currentDate = list.getString("dt_txt")
                    val minTemp = main.getString("temp_min") + "°C"
                    val maxTemp = main.getString("temp_max") + "°C"

                    val windSpeed = wind.getString("speed") + "m/s"
                    val windDirect = getCardinalDirections(wind.getString("deg"))

                    val minAndmaxTemp = "Max Temp: $maxTemp, Min Temp: $minTemp"
                    val windSpeedAndDir = "Wind: $windDirect at $windSpeed"


                    //removes the unnecessary time element from string
                    val strs = currentDate.split(" ").toTypedArray()
                    //Log.d("Debug", "did it work: " + strs[0])

                    currentDate = strs[0]








                    //this ensures that each text element is filled correctly and nothing is overwritten
                    when {
                        checkDate == currentDate -> {
                            index++
                        }
                        count == 0 -> {
                            findViewById<TextView>(R.id.day2date).text = currentDate
                            findViewById<TextView>(R.id.day2temp).text = minAndmaxTemp
                            findViewById<TextView>(R.id.day2wind).text = windSpeedAndDir
                            count++
                            checkDate = currentDate
                        }
                        count == 1 -> {
                            findViewById<TextView>(R.id.day3date).text = currentDate
                            findViewById<TextView>(R.id.day3temp).text = minAndmaxTemp
                            findViewById<TextView>(R.id.day3wind).text = windSpeedAndDir
                            count++
                            checkDate = currentDate
                        }
                        count == 2 -> {
                            findViewById<TextView>(R.id.day4date).text = currentDate
                            findViewById<TextView>(R.id.day4temp).text = minAndmaxTemp
                            findViewById<TextView>(R.id.day4wind).text = windSpeedAndDir
                            count++
                            checkDate = currentDate
                        }
                        count == 3 -> {
                            findViewById<TextView>(R.id.day5date).text = currentDate
                            findViewById<TextView>(R.id.day5temp).text = minAndmaxTemp
                            findViewById<TextView>(R.id.day5wind).text = windSpeedAndDir
                            count++
                            checkDate = currentDate
                        }
                    }


                    findViewById<ProgressBar>(R.id.idloading).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.RelPar).visibility = View.VISIBLE

                } catch (e: Exception) {

                    return;
                }
            }
        }


        fun getCardinalDirections(degrees: String): String {
            //Function used to change the less UX friendly degrees into cardinal directions
            val degasInt = degrees.toDouble()
            val directions = arrayOf("↑ N", "↗ NE", "→ E", "↘ SE", "↓ S", "↙ SW", "← W", "↖ NW")
            val doubAngle = (Math.round(degasInt / 45)) % 8
            val angle = doubAngle.toInt()
            return directions[angle]
        }

    }
}