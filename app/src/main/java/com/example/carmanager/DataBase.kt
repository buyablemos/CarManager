package com.example.carmanager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context,factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "RefuelingDB"
        private const val TABLE_REFUELINGS = "refuelings"
        private const val KEY_ID = "id"
        private const val KEY_DATE = "date"
        private const val KEY_FUEL_AMOUNT = "fuel_amount"
        private const val KEY_PRICE = "price"
        private const val KEY_DISTANCE = "distance"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_REFUELINGS("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_DATE DATETIME,"
                + "$KEY_FUEL_AMOUNT REAL,"
                + "$KEY_PRICE REAL,"
                + "$KEY_DISTANCE REAL)")
        db?.execSQL(createTable)

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_REFUELINGS")
        onCreate(db)
    }

    fun addRefueling(refueling: Refueling) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_DATE, refueling.date)
        values.put(KEY_FUEL_AMOUNT, refueling.fuelAmount)
        values.put(KEY_PRICE, refueling.price)
        values.put(KEY_DISTANCE, refueling.distance)
        db.insert(TABLE_REFUELINGS, null, values)
        db.close()
    }


    @SuppressLint("Range")
    fun getFuelingHistory(): List<Refueling> {



        val historyList = mutableListOf<Refueling>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REFUELINGS", null)
        var last: Refueling?=null
        if (cursor.moveToFirst()) {
            do {
                var averageEconomy:Double? = null
                var distanceFromLast:Double? = null
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val fuelAmount = cursor.getDouble(cursor.getColumnIndex(KEY_FUEL_AMOUNT))
                val price = cursor.getDouble(cursor.getColumnIndex(KEY_PRICE))
                val distance = cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE))
                if(last!=null) {
                    averageEconomy = fuelAmount/(distance- last.distance)*100.0
                    distanceFromLast=distance-last.distance
                }

                val refueling = Refueling(id, date, fuelAmount, price, distance, averageEconomy,distanceFromLast)
                historyList.add(refueling)
                last=refueling
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historyList
    }

    @SuppressLint("Range")
    fun getLastRefuel(): Refueling? {

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REFUELINGS", null)
        var refueling: Refueling?=null
        if (cursor.moveToLast()) {

                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val fuelAmount = cursor.getDouble(cursor.getColumnIndex(KEY_FUEL_AMOUNT))
                val price = cursor.getDouble(cursor.getColumnIndex(KEY_PRICE))
                val distance = cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE))
                refueling = Refueling(id, date, fuelAmount, price, distance,null,null)

        }
        cursor.close()
        db.close()
        return refueling
    }

    fun deleteRecord(id: Long) {
        val db = this.writableDatabase

        db.execSQL("DELETE FROM $TABLE_REFUELINGS WHERE $KEY_ID = ?", arrayOf(id.toString()))

        db.close()
    }

}
