package com.finderbar.jovian.adaptor.tutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.finderbar.jovian.R
import com.finderbar.jovian.models.Tutorial

class TutorialAdaptor(private val context: Context, private val dataSource: MutableList<Tutorial>): BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val views = inflater.inflate(R.layout.item_tutorial_list, parent, false)
        val datum = dataSource[position];
        val title = views.findViewById(R.id.title) as TextView
            title.text = datum.titleText

        return views;
    }

    override fun getItem(position: Int): Any = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong();

    override fun getCount(): Int = dataSource.size

}