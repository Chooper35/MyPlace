package com.ayberkduzova.myplaces

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.custom_list_row.view.*

class CustomAdapter( private val placeList :MutableList<Place>, private val context:Activity) :
    ArrayAdapter<Place>(context, R.layout.custom_list_row, placeList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.layoutInflater
        val customView = layoutInflater.inflate(R.layout.custom_list_row,null,true)
        customView.rowTextView.text =placeList.get(position).address
        return customView

    }
}