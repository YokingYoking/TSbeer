package com.example.tsbeer.ui.dashboard

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.example.tsbeer.MyApplication
import com.example.tsbeer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class CartListAdapter(activity: FragmentActivity, itemList: ArrayList<Map<String, Any>>) : BaseAdapter(){
    private var activity: Activity? = null
    private var itemList: ArrayList<Map<String, Any>>? = null
    var mConnMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var myApp = activity?.application as MyApplication

    init {
        this.activity = activity
        this.itemList = itemList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var view: View

        if(convertView==null){
            view = View.inflate(activity, R.layout.cart_items,null)
            holder = ViewHolder(view)
            view.tag = holder
        }else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.deleteBtn.setOnClickListener {
            loadData(position)
            itemList?.removeAt(position)
            notifyDataSetChanged()
        }

        holder.item_name.text = itemList?.get(position)?.get("name").toString()
        var itemPrice: Double = itemList?.get(position)?.get("price") as Double
        var amount: Int = itemList?.get(position)?.get("amount") as Int
        var totalPrice = itemPrice * amount
        holder.price.text = activity?.resources?.getString(R.string.total_item_price) +" $ " + totalPrice.toString()
        holder.amount.text = activity?.resources?.getString(R.string.amount_tv) + " " + itemList?.get(position)?.get("amount").toString()
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


    class ViewHolder(var view: View){
        var item_name: TextView = view.findViewById(R.id.cartItem_name)
        var price: TextView = view.findViewById(R.id.cart_price)
        var img: ImageView = view.findViewById(R.id.cart_img)
        var amount: TextView = view.findViewById(R.id.cart_amount)
        var deleteBtn: Button = view.findViewById(R.id.deleteBtn)
    }

    private fun loadData(position: Int) {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/deleteCartItem"
        //Bullet proofing test to make sure connection manager reference is not null
        if (mConnMgr != null) {
            // Get active network info
            val networkInfo = mConnMgr!!.activeNetworkInfo
            //If any activie network is available and inernet connection is available
            if (networkInfo != null) { // && networkInfo.isConnected
                //Start to data download by coroutine
                loadDataByCoroutines(path, position)
            } else {
                //If network is off of Internet is not availble, inform the user
                Toast.makeText(activity, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDataByCoroutines(path: String, position: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            var data: String? = null
            val inStream: InputStream
            val outStream: OutputStream
            // create a URL Connection object and set its parameters
            val url = URL(path)
            val urlConn = url.openConnection() as HttpURLConnection
            try {
                urlConn.connectTimeout = 5000
                urlConn.readTimeout = 2500
                // Set HTTP request method
                urlConn.requestMethod = "POST"
                urlConn.doInput = true
                urlConn.doOutput = true
                urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8")
                //Perform network request
                urlConn.connect()
                val body =
                    "{\"username\":" + "\""+ myApp.name + "\"" + ","+ "\"" + "itemId" + "\"" + ":"  + "\"" + itemList?.get(position)?.get("itemId").toString()  + "\"" + "}"
                outStream = urlConn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(outStream))
                writer.write(body)
                writer.close()
                //After the network response,retrieve the input stream
                inStream = urlConn.inputStream
                // convert the input stream to String Bitmap
                data = readStream(inStream)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (urlConn != null) {
                    urlConn.disconnect()
                }
            }
            launch(Dispatchers.Main) {
                if (data != null) {
                    try {
                        val reader = JSONObject(data)
                        val successBoolean: Boolean = reader.getBoolean("success")
                        val msg: String = reader.getString("message")
                        if (successBoolean) {

                        } else {
                        }
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun readStream(`in`: InputStream): String {
        var reader: BufferedReader? = null
        val data = StringBuffer("")
        try {
            reader = BufferedReader(InputStreamReader(`in`))
            var line: String? = ""
            while (reader.readLine().also { line = it } != null) {
                data.append(line)
            }
        } catch (e: IOException) {
            Log.e("HttpGetTask", "IOException")
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return data.toString()
    }

}