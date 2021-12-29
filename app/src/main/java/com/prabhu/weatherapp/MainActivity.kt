package com.prabhu.weatherapp

import android.Manifest
import android.content.pm.PackageManager

import android.location.Geocoder

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private  var homerl: RelativeLayout?=null
    private  var loadingpb: ProgressBar?=null
    private  var citynametv: TextView?=null
    private  var temperatures: TextView?=null
    private  var conditiontv: TextView?=null
    private lateinit var weatherrv: RecyclerView
    private  var cityedt: TextInputEditText?=null
    private  var backiv: ImageView?=null
    private  var iconiv: ImageView?=null
    private  var searchiv: ImageView?=null
    private  var weatherrvmodelarraylist= ArrayList<weatherrvmodel>()
    private var weatherrvadapters:weatherrvadapter= weatherrvadapter(weatherrvmodelarraylist)
    private var locationManager : LocationManager? = null
    private var permissioncode: Int? = null
    private  var cityName:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)

        homerl = findViewById(R.id.idrlhome)
        loadingpb = findViewById(R.id.idpbloading)
        citynametv = findViewById(R.id.idtvcityname)
        temperatures = findViewById(R.id.idtvtemperatures)
        conditiontv = findViewById(R.id.idtvcondition)
        weatherrv= findViewById(R.id.idrvweather)
        cityedt = findViewById(R.id.idedtcity)
        backiv = findViewById(R.id.idivback)
        iconiv = findViewById(R.id.idivicon)
        searchiv = findViewById(R.id.idivsearch)

        permissioncode=1




        weatherrv.adapter=weatherrvadapters



        // on the below line we are notifying our adapter as the data is updated.
        weatherrvadapters.notifyDataSetChanged()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                permissioncode?.let {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), it
                    )
                }
            } else {
                permissioncode?.let {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), it
                    )
                }
            }
        }

val location= locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            cityName=getcityname(location.longitude,location.latitude)
        }

        cityName?.let { getweatherinfo(it) }

        searchiv!!.setOnClickListener {
            val city:String=cityedt!!.text.toString()
            if (city.isEmpty()){
                Toast.makeText(this, "Please Enter Valid City Name", Toast.LENGTH_SHORT).show()
            }else{
                citynametv!!.text=cityName
                getweatherinfo(city)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode==permissioncode){
        if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Please Provide the Permissions", Toast.LENGTH_SHORT).show()
              finish()
        }
    }
    }

private fun getcityname(longitude:Double,latitude:Double):String{
    var cname="Not found"
    val gcd=Geocoder(baseContext,Locale.getDefault())
    try {
        val address=gcd.getFromLocation(latitude,longitude,10)
        for (i in address){
            if (i!=null){
                val city:String=i.locality
                if (city.isNotEmpty()){
                    cname=city

                }else{
                    Log.d("TAG","CITY NOT FOUND")
                    Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }catch (e:IOException){
        e.printStackTrace()
    }
    return cname
}

 private fun getweatherinfo(cinema:String){
     val url="https://api.weatherapi.com/v1/forecast.json?key=8c4dfdc5e32e4198bae151330212712&q=$cinema&days=1&aqi=yes&alerts=yes"
     val queue = Volley.newRequestQueue(this@MainActivity)
     citynametv!!.text=cinema
     loadingpb!!.visibility = View.GONE
     homerl!!.visibility=View.VISIBLE

     val request = JsonObjectRequest(
             Request.Method.GET, url, null, { response ->

             Log.e("TAG", "SUCCESS RESPONSE IS $response")

                 loadingpb!!.visibility = View.GONE
                 homerl!!.visibility=View.VISIBLE
                 weatherrvmodelarraylist?.clear()

             try {
                 val temperature:String=response.getJSONObject("current").getString("temp_c")
              val samp= "$temperature°c"
                 temperatures!!.text = samp
                val isday:Int=response.getJSONObject("current").getInt("is_day")
                 val condition:String=response.getJSONObject("current").getJSONObject("condition").getString("text")
                 val conditionicon:String=response.getJSONObject("current").getJSONObject("condition").getString("icon")
                 Picasso.get().load("http:$conditionicon").into(iconiv)
                 conditiontv!!.text=condition
              if (isday==1){
                  Picasso.get().load("https://images.unsplash.com/photo-1434281406913-47acccb03654?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=385&q=80").into(backiv)
              }else{
                  Picasso.get().load("https://images.unsplash.com/photo-1507400492013-162706c8c05e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1559&q=80").into(backiv)
              }

                 val forecastObj=response.getJSONObject("forecast")
                 val forecastO=forecastObj.getJSONArray("forecastday").getJSONObject(0)
                 val hourarray = forecastO.getJSONArray("hour")

                 for (i in 0 until hourarray.length()) {


                     val hourObj = hourarray.getJSONObject(i)

                     // on below line we are getting data from our session
                     // object and we are storing that in a different variable.
                     val time: String = hourObj.getString("time")
                     val temper: String = hourObj.getString("temp_c")
                     val img: String = hourObj.getJSONObject("condition").getString("icon")
                     val windspd: String = hourObj.getString("wind_kph")
//                        val img:Int=isday
                     weatherrvmodelarraylist?.add(weatherrvmodel(time,temper,img,windspd))


                 }

                 weatherrvadapters?.notifyDataSetChanged()

             } catch (e: JSONException) {
                 Toast.makeText(this@MainActivity, "error when fetching JSON", Toast.LENGTH_SHORT).show()
                 // below line is for handling json exception.
                 e.printStackTrace()
             }
         },
             { error ->

                 Log.e("TAG", "RESPONSE IS $error")

                 Toast.makeText(this@MainActivity, "Please Enter Valid City Name", Toast.LENGTH_SHORT).show()
             })

     queue.add(request)
 }


}