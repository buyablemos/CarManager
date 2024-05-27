package com.example.carmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class HistoryActivity : AppCompatActivity() {


    private val REQUEST_CODE=250
    private lateinit var recyclerView: RecyclerView
    private var sortingType:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = HistoryAdapter(getFuelingHistory())
        recyclerView.adapter = adapter


        findViewById<Button>(R.id.btn_back2).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_add).setOnClickListener {
            val intent = Intent(this, AddRefuelingActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        findViewById<Button>(R.id.buttonSort).setOnClickListener(){
            if(sortingType!=null){
            sortingType?.let { sorted(it) }
            }
            else{
                Toast.makeText(this,"Zaznacz typ sortowania!",Toast.LENGTH_SHORT).show()
            }

        }

        val spinner: Spinner = findViewById(R.id.spinner)
        var isSpinnerInitialized = false
        val sortOptions = mutableListOf("Sortuj według śr. spalania", "Sortuj według daty", "Sortuj według ceny za litr")

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sortOptions
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isSpinnerInitialized == true) {
                    val selectedOption = sortOptions[position]
                    Toast.makeText(
                        this@HistoryActivity,
                        "Wybrano: $selectedOption",
                        Toast.LENGTH_SHORT
                    ).show()

                    sortingType=position

                }
                else
                {isSpinnerInitialized=true
                    sortingType=position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


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

    fun sorted(position: Int){
        var adapter:HistoryAdapter?=null
        val history = getFuelingHistory()
        when(position) {
            0-> {
                adapter = HistoryAdapter(history.filter { it.averageFuelEconomy!=null }.sortedBy { it.averageFuelEconomy})
            }
            1-> {
                adapter = HistoryAdapter(history.sortedWith(compareBy { LocalDate.parse(it.date) }))
            }
            2-> {
                adapter = HistoryAdapter(history.sortedWith(compareBy { it.price/it.fuelAmount }))
            }
        }
        recyclerView.adapter = adapter
    }
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val adapter = HistoryAdapter(getFuelingHistory())
                recyclerView.adapter = adapter

            }
        }
    }




    private fun getFuelingHistory(): List<Refueling> {
        val dbHelper = DatabaseHelper(this)
        return dbHelper.getFuelingHistory()
    }

    private fun getFakeFuelingHistory(): List<Refueling> {
        val fakeHistory = mutableListOf<Refueling>()
        fakeHistory.add(Refueling(1, "2024-05-20", 45.5, 200.0, 300.0,15.0,500.0))
        fakeHistory.add(Refueling(2, "2024-05-15", 38.2, 180.0, 280.0,12.0,500.0))
        fakeHistory.add(Refueling(3, "2024-05-10", 42.8, 190.0, 290.0,9.0,500.0))
        fakeHistory.add(Refueling(4, "2024-05-05", 36.7, 170.0, 270.0,7.5,500.0))
        return fakeHistory
    }

    private inner class HistoryAdapter(private val historyList: List<Refueling>) :
        RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false)
            return HistoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            val refueling = historyList[position]
            holder.bind(refueling)
        }

        override fun getItemCount(): Int {
            return historyList.size
        }



        inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dateTextView: TextView = itemView.findViewById(R.id.text_date)
            private val detailsTextView: TextView = itemView.findViewById(R.id.text_details)
            private val button: Button = itemView.findViewById(R.id.buttonDelete)


            fun bind(refueling: Refueling) {
                val decimalFormat = DecimalFormat("#.##")
                if(refueling.distanceFromLastFueling!=null&&refueling.averageFuelEconomy!=null) {
                    dateTextView.text = refueling.date
                    val details =
                        "Paliwo: ${refueling.fuelAmount} L, Cena: ${refueling.price} PLN, Przebieg: ${refueling.distance} km, Dystans pokonany: ${refueling.distanceFromLastFueling},Średnie spalanie l/100km: ${decimalFormat.format(refueling.averageFuelEconomy)}, Cena za litr paliwa: ${decimalFormat.format(refueling.price/refueling.fuelAmount)}"
                    detailsTextView.text = details

                    button.setOnClickListener(){
                        val db=DatabaseHelper(this@HistoryActivity)
                        db.deleteRecord(refueling.id)
                        val adapter = HistoryAdapter(getFuelingHistory())
                        recyclerView.adapter = adapter
                    }
                }
                else{
                    dateTextView.text = refueling.date
                    val details =
                        "Paliwo: ${refueling.fuelAmount} L, Cena: ${refueling.price} PLN, Przebieg: ${refueling.distance} km, , Cena za litr paliwa: ${decimalFormat.format(refueling.price/refueling.fuelAmount)}"
                    detailsTextView.text = details
                    button.setOnClickListener(){
                        val db=DatabaseHelper(this@HistoryActivity)
                        db.deleteRecord(refueling.id)
                        val adapter = HistoryAdapter(getFuelingHistory())
                        recyclerView.adapter = adapter

                    }
                }

            }
        }
    }

}
