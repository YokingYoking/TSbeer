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
    private lateinit var mFilter: MyFilter
    //必须存放两个String[]类型数据，一个保存原始数据，一个用来展示过滤后的数据
    private var displayItem: ArrayList<Map<String, Any>>? = null

    init {
        this.activity = activity
        this.itemList = itemList
        displayItem = itemList
    }

    inner class MyFilter : Filter() {
        override fun performFiltering(prefix: CharSequence?): FilterResults {
            val results = FilterResults()
            if (prefix == null || prefix.isEmpty()) {
                results.values = itemList
                results.count = itemList?.size!!
            } else {
                val prefixString = prefix.toString()
                val newValues: ArrayList<Map<String, Any>>? = null
                for (i in 0 until itemList?.size!!) {
                    val value: String = itemList?.get(i)?.get("name").toString()
                    if (value == prefixString) { //我这里的规则就是筛选出和prefix相同的元素
                        val item = itemList?.get(i)
                        if (item != null) {
                            newValues?.add(item)
                        }
                    }
                }
                results.values = newValues
                if (newValues != null) {
                    results.count = newValues.size
                }
            }
            return results
        }

        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults
        ) {
            displayItem = results.values as ArrayList<Map<String, Any>>?
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
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

        holder.item_name.text = displayItem?.get(position)?.get("name").toString()
        holder.price.text = "￥ " + displayItem?.get(position)?.get("price").toString()
        GlobalScope.launch(Dispatchers.IO) {
            val myurl = URL(displayItem?.get(position)?.get("imgUrl").toString())
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

//返回过滤器
    fun getFilter(): MyFilter {
        mFilter = MyFilter()
        return mFilter
    }
}