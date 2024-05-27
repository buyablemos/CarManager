package com.example.carmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRefuelingActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextFuelAmount: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextDistance: EditText
    private lateinit var buttonAddRefueling: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_refueling)

        editTextDate = findViewById(R.id.edit_text_date)
        editTextFuelAmount = findViewById(R.id.edit_text_fuel_amount)
        editTextPrice = findViewById(R.id.edit_text_price)
        buttonAddRefueling = findViewById(R.id.button_add_refueling)
        editTextDistance = findViewById(R.id.edit_text_distance)

        buttonAddRefueling.setOnClickListener {

            addRefueling()
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        findViewById<Button>(R.id.button_back3).setOnClickListener {
            finish()
        }
    }

    private fun parseDate(dateStr: String): Date {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateStr) ?: Date(0)
        } catch (e: Exception) {
            e.printStackTrace()
            Date(0)
        }
    }

    private fun addRefueling() {

        if(parseDate(editTextDate.text.toString())==Date(0)){
            return
        }
        val date=editTextDate.text.toString()
        val fuelAmount = editTextFuelAmount.text.toString().toDoubleOrNull() ?: return
        val price = editTextPrice.text.toString().toDoubleOrNull() ?: return
        val distance =editTextDistance.text.toString().toDoubleOrNull()?: return

        val refueling = Refueling(date = date, fuelAmount = fuelAmount, price = price, distance = distance, averageFuelEconomy = null, distanceFromLastFueling = null)

        val db=DatabaseHelper(this)
        db.addRefueling(refueling=refueling)


    }
}
