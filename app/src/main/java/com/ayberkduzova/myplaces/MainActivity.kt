package com.ayberkduzova.myplaces

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog



class MainActivity : AppCompatActivity() {

    var placesArray : MutableList<Place> = mutableListOf()



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place,menu)
        val deleteInflater = menuInflater
        deleteInflater.inflate(R.menu.delete_all,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_place_option)
        {
            val intent= Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        if(item.itemId==R.id.delete_place_option)
        {

            val dialog= AlertDialog.Builder(this@MainActivity)
            dialog.setCancelable(false)
            dialog.setTitle("Do you want to delete all?")
            dialog.setPositiveButton("Yes")
            {
                    dialog,which->
                        val database= openOrCreateDatabase("PlacesDB", Context.MODE_PRIVATE,null)
                        database.execSQL("DELETE FROM places")
                Toast.makeText(this@MainActivity,"Deleted", Toast.LENGTH_LONG).show()
                finish()
                overridePendingTransition(0, 0);
                startActivity(getIntent())
                overridePendingTransition(0, 0);
            }.setNegativeButton("No")
            {
                    dialog,which-> Toast.makeText(this@MainActivity,"Canceled!", Toast.LENGTH_LONG).show()
            }
            dialog.show()

        }
        return super.onOptionsItemSelected(item)
    }
    var willDeleteId : Int?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try
        {
            val database= openOrCreateDatabase("PlacesDB", Context.MODE_PRIVATE,null)
            val cursor=database.rawQuery("SELECT * FROM places",null)
            var placeId= cursor.getColumnIndex("id")
            val addressIndex=cursor.getColumnIndex("address")
            val latitudeIndex=cursor.getColumnIndex("latitude")
            val longitudeIndex=cursor.getColumnIndex("longitude")

            while(cursor.moveToNext())
            {
                val idFromDB=cursor.getInt(placeId)
                willDeleteId=idFromDB
                val addressFromDB=cursor.getString(addressIndex)
                val latitudeFromDB=cursor.getDouble(latitudeIndex)
                val longitudeFromDB=cursor.getDouble(longitudeIndex)
                val myPlace=Place(addressFromDB,latitudeFromDB,longitudeFromDB)
                println(" Database ID's"  +idFromDB)
                placesArray.add(myPlace)
            }
            cursor.close()
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }
        val customAdapter = CustomAdapter(placesArray,this)
        listView.adapter=customAdapter
        listView.setOnItemClickListener{parent, view, position, id ->
            val intent = Intent(this@MainActivity,MapsActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("selectedPlace",placesArray.get(position))
            startActivity(intent)
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            var which_item : Int? = position
            val dialog= AlertDialog.Builder(this@MainActivity)
            dialog.setCancelable(false)
            dialog.setTitle("Do you want to delete this place")
            dialog.setPositiveButton("Yes")
            {
                    dialog,which->
                try
                {
                    if (which_item != null) {
                        val database= openOrCreateDatabase("PlacesDB", Context.MODE_PRIVATE,null)
                        var willDeleteID : Int = position +1
                        database.execSQL("DELETE FROM places WHERE id = $willDeleteID")
                        placesArray.removeAt(position)
                        customAdapter.notifyDataSetChanged()
                    }
                }
                catch(e:Exception)
                {
                    e.printStackTrace()
                }
                Toast.makeText(this@MainActivity,"Deleted", Toast.LENGTH_LONG).show()
            }.setNegativeButton("No")
            {
                    dialog,which-> Toast.makeText(this@MainActivity,"Canceled!", Toast.LENGTH_LONG).show()
            }
            dialog.show()
            true
        }
    }

}
