package com.example.carmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val db=DatabaseHelper(this)
        val refuel=db.getLastRefuel()
        var stringToDistance = "Ostatni znany przebieg samochodu: "
        if(refuel!=null) {
            stringToDistance += refuel.distance.toString()
            findViewById<TextView>(R.id.textViewprzebieg).text =
                (stringToDistance)
        }
    }

}