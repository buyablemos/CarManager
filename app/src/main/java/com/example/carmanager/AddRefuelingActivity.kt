package com.example.carmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

            val status=addRefueling()

            if (status==0){
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()}
        }

        findViewById<Button>(R.id.button_back3).setOnClickListener {
            finish()
        }
        val imageView = findViewById<ImageView>(R.id.imageView3)
        val imageView2 = findViewById<ImageView>(R.id.imageView4)

        var gifUri = "android.resource://" + packageName + "/" + R.raw.icons8
        Glide.with(this)
            .asGif()
            .load(gifUri)
            .into(imageView2)
        gifUri = "android.resource://" + packageName + "/" + R.raw.refuel
        Glide.with(this)
            .asGif()
            .load(gifUri)
            .into(imageView)
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

    private fun addRefueling(): Int {
        val db=DatabaseHelper(this)
        val history=db.getFuelingHistory()

        if(parseDate(editTextDate.text.toString())==Date(0)){
            Toast.makeText(this,"Data nie jest poprawna",Toast.LENGTH_SHORT).show()
            return 1
        }
        val date=editTextDate.text.toString()
        val fuelAmount = editTextFuelAmount.text.toString().toDoubleOrNull() ?: return 1
        val price = editTextPrice.text.toString().toDoubleOrNull() ?: return 1
        val distance =editTextDistance.text.toString().toDoubleOrNull()?: return 1
        val filtered=history.filter { it.distance >= distance }
        if(filtered.isNotEmpty()){
            Toast.makeText(this,"Przebieg jest mniejszy, niż ostatnio zapisany spróbuj jeszcze raz",Toast.LENGTH_SHORT).show()
            return 1
        }

        val refueling = Refueling(date = date, fuelAmount = fuelAmount, price = price, distance = distance, averageFuelEconomy = null, distanceFromLastFueling = null)


        db.addRefueling(refueling=refueling)

        return 0
    }
}
