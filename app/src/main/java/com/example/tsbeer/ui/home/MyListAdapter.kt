package com.example.tsbeer.ui.home

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.example.tsbeer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


class MyListAdapter(activity: FragmentActivity, itemList: ArrayList<Map<String, Any>>) : BaseAdapter() {
    private var activity: Activity? = null
    private var itemList: ArrayList<Map<String, Any>>? = null

    init {
        this.activity = activity
        this.itemList = itemList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var view:View

        if(convertView==null){
            view = View.inflate(activity, R.layout.list_items,null)
            holder = ViewHolder(view)
            view.tag = holder
        }else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.item_name.text = itemList?.get(position)?.get("name").toString()
        holder.price.text = "￥ " + itemList?.get(position)?.get("price").toString()
        GlobalScope.launch(Dispatchers.IO) {
            val myurl = URL(itemList?.get(position)?.get("imgUrl").toString())
            val httpURLConnection = myurl.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            var bitmap = BitmapFactory.decodeStream(httpURLConnection.inputStream)
            launch(Dispatchers.Main) {
                holder.img.setImageBitmap(bitmap)
            }
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return itemList?.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return itemList?.size!!
    }

//    private fun getImageByCoroutines(url: String) {
//
//    }


    class ViewHolder(var view:View){
        var item_name:TextView = view.findViewById(R.id.cartItem_name)
        var price: TextView = view.findViewById(R.id.cart_price)
        var img: ImageView = view.findViewById(R.id.cart_img)
    }

}