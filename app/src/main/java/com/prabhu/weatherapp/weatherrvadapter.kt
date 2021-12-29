package com.prabhu.weatherapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class weatherrvadapter (private val wlist:List<weatherrvmodel>): RecyclerView.Adapter<weatherrvadapter.weatherviewholder>() {
    class weatherviewholder(itemview : View ): RecyclerView.ViewHolder(itemview){
        val windtv:TextView=itemview.findViewById(R.id.idtvwindspeed)
        val temperaturetv:TextView=itemview.findViewById(R.id.idtvtemperature)
        val timetv:TextView=itemview.findViewById(R.id.idtvtime)

        val conditioniv:ImageView=itemview.findViewById(R.id.idivcondition)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): weatherviewholder {

        val itemX = LayoutInflater.from(parent.context).inflate(R.layout.weather_rv_item,parent,false)
        return weatherviewholder(itemX)

    }

    override fun onBindViewHolder(holder: weatherviewholder, position: Int) {

        var y:weatherrvmodel= wlist[position]


//        if (y.icon==1){
//            Picasso.get().load("https://images.unsplash.com/photo-1434281406913-47acccb03654?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=385&q=80").into(holder.conditioniv)
//        }else{
//            Picasso.get().load("https://images.unsplash.com/photo-1507400492013-162706c8c05e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1559&q=80").into(holder.conditioniv)
//        }

        Picasso.get().load("https:${y.icon}").into(holder.conditioniv)
//        Picasso.get().load(y.img).into(holder.conditioniv)
        holder.temperaturetv.text="${y.temperature} °c"
        holder.windtv.text="${y.windspeed} Km/h"
        val input= SimpleDateFormat("yyyy-MM-dd hh:mm")
        val output = SimpleDateFormat("hh:mm aa")
        try {
            var t=input.parse(y.time)
            holder.timetv.text=output.format(t)

        }catch (e:ParseException){
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return wlist.size
    }
}