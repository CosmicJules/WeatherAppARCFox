package com.example.weatherapparcfox

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //This MainActivity is used to show the current weather stats

    //API gained from OpenWeatherMap API
    val API: String = "3a720b3a3e177f56b462b62605099cdf"
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //button to change to forecast Activity
        val button: Button = findViewById(R.id.toForecast)

        button.setOnClickListener() {
            val intent = Intent(this, Forecast::class.java)
            startActivity(intent)
        }

        supportActionBar?.hide()
        weatherDetails().execute()


    }

    inner class weatherDetails() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            //show progress bar and hide main view whilst loading data
            findViewById<ProgressBar>(R.id.idloading).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.RelPar).visibility = View.GONE

        }

        override fun doInBackground(vararg p0: String?): String? {
            //load in data from Openweathermap current weather API
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?id=2655984&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {

                //load elements required from JSON
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val wind = jsonObj.getJSONObject("wind")
                val sys = jsonObj.getJSONObject("sys")

                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val temp = main.getString("temp") + "°C"
                val weatherDesc = weather.getString("main")
                val minTemp = main.getString("temp_min") + "°C"
                val maxTemp = main.getString("temp_max") + "°C"

                val sunRise: Long = sys.getLong("sunrise")
                val sunSet: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + "m/s"
                val windDirect = getCardinalDirections(wind.getString("deg"))
                val cityName = jsonObj.getString("name") + ", " + sys.getString("country")
                val weatherIcon = weather.getString("icon")




                findViewById<TextView>(R.id.cityName).text = cityName
                //get date from device to display on banner in format Sunday, December 19, 2021
                val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy")
                val currentDate = sdf.format(Date())
                findViewById<TextView>(R.id.currDate).text = currentDate

                findViewById<TextView>(R.id.currentTemperature).text = temp
                findViewById<TextView>(R.id.weatherDesc).text = weatherDesc
                findViewById<TextView>(R.id.minTemp).text = minTemp
                findViewById<TextView>(R.id.maxTemp).text = maxTemp
                //display time in am format for sunrise and sunset
                findViewById<TextView>(R.id.sunriseTime).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunRise * 1000))
                findViewById<TextView>(R.id.sunsetTime).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunSet * 1000))
                findViewById<TextView>(R.id.windSpeed).text = windSpeed
                findViewById<TextView>(R.id.windDirect).text = windDirect


                //using Picasso to get the Icon from OpenWeatherMap
                imageView = findViewById(R.id.imageView)
                Picasso.get().load("https://openweathermap.org/img/w/$weatherIcon.png")
                    .into(imageView)


                //load main screen back into view and hide porgress bar
                findViewById<ProgressBar>(R.id.idloading).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.RelPar).visibility = View.VISIBLE


            } catch (e: Exception) {

                return;
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