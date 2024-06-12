package com.example.carmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.open_map_button).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.fuel_history_button).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        val imageView = findViewById<ImageView>(R.id.imageView)


        val gifUri = "android.resource://" + packageName + "/" + R.raw.minicar
        Glide.with(this)
            .asGif()
            .load(gifUri)
            .into(imageView)



        val db=DatabaseHelper(this)
        val refuel=db.getLastRefuel()
        var stringToDistance = "Ostatni przebieg: "
        if(refuel!=null) {
            stringToDistance += refuel.distance.toString()
            findViewById<TextView>(R.id.textViewprzebieg).text =
                (stringToDistance)
        }
    }

    override fun onResume() {
        super.onResume()
        val db=DatabaseHelper(this)
        val refuel=db.getLastRefuel()
        var stringToDistance = "Ostatni przebieg: "
        if(refuel!=null) {
            stringToDistance += refuel.distance.toString()
            findViewById<TextView>(R.id.textViewprzebieg).text =
                (stringToDistance)
        }
    }

}