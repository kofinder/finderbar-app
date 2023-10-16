package com.finderbar.jovian.utilities.phonefield

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.finderbar.jovian.R


/**
 * Created by thein on 12/7/18.
 */
class CountriesAdapter(context: Context,
                       countries: kotlin.collections.List<Country>) :
        ArrayAdapter<Country>(context, R.layout.item_country, R.id.name, countries),
        SpinnerAdapter {

    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(getContext())
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val country = getItem(position)
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_spinner, parent, false)
        }
        val imageView = convertView?.findViewById<View>(R.id.flag) as ImageView
        val textView = convertView?.findViewById<View>(R.id.dial_code) as TextView


        textView.text = '+'+ country.dialCode.toString()
        imageView.setImageResource(country!!.getResId(context))
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        parent.setPadding(0, 0, 0, 0);
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_country, parent, false)
            viewHolder = ViewHolder()
            viewHolder.mName = convertView!!.findViewById<View>(R.id.name) as TextView
            viewHolder.mDialCode = convertView.findViewById<View>(R.id.dial_code) as TextView
            viewHolder.mFlag = convertView.findViewById<View>(R.id.flag) as ImageView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val country = getItem(position)
        viewHolder.mFlag!!.setImageResource(country!!.getResId(context))
        viewHolder.mName!!.text = country.displayName
        viewHolder.mDialCode!!.text = '+' + country.dialCode.toString()
        return convertView
    }

    private class ViewHolder {
        internal var mName: TextView? = null
        internal var mDialCode: TextView? = null
        internal var mFlag: ImageView? = null
    }
}