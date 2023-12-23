package com.instantrip.presentation.message
//https://www.youtube.com/watch?v=MeG-0MVP3jw

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.instantrip.R

class MessageListAdapter (private val context: Context, private val messageCardData: ArrayList<String>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return messageCardData.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //inflate layout message_card_view.xml
        val view = LayoutInflater.from(context).inflate(R.layout.message_card_view, container, false)

        //get data
        //val model = myModelArrayList[position]
        val text = messageCardData[position]

        //set data to ui views
        val title: TextView = view.findViewById(R.id.message_text);
        title.text = text

        view.setOnClickListener{
            Toast.makeText(context, "$text", Toast.LENGTH_SHORT).show()
        }

        container.addView(view, position)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}