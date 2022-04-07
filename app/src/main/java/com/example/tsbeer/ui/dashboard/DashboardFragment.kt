package com.example.tsbeer.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tsbeer.MyApplication
import com.example.tsbeer.R
import com.example.tsbeer.databinding.FragmentDashboardBinding
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

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    lateinit var mConnMgr: ConnectivityManager
    lateinit var mCartListView: ListView
    private var list = ArrayList<Map<String, Any>>()
    lateinit var myApp: MyApplication
    lateinit var mLoginFirst: TextView
    lateinit var mSubmitOrderBtn: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        myApp = activity?.application as MyApplication
        mLoginFirst = binding.loginFirstTv
        mSubmitOrderBtn = binding.submitOrderBtn
        mSubmitOrderBtn.isEnabled = false
        if (myApp.name == "") {
             mLoginFirst.text = resources.getString(R.string.login_first)
            mSubmitOrderBtn.isEnabled = false
        } else {
            // Store the Connectivity Manager in the member variable
            mConnMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            mCartListView = binding.cartListView
            loadData()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSubmitOrderBtn.setOnClickListener{
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle(R.string.submit_order)
                    .setMessage(R.string.submit_dialog)
                    .setPositiveButton("OK") {dialog, which ->
                        delData()
                    }
                    .setNegativeButton("CANCEL") {dialog, which ->

                    }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun delData() {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/clearCart"
        //Bullet proofing test to make sure connection manager reference is not null
        if (mConnMgr != null) {
            // Get active network info
            val networkInfo = mConnMgr!!.activeNetworkInfo
            //If any activie network is available and inernet connection is available
            if (networkInfo != null) { // && networkInfo.isConnected
                //Start to data download by coroutine
                delDataByCoroutines(path)
            } else {
                //If network is off of Internet is not availble, inform the user
                Toast.makeText(activity, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun delDataByCoroutines(path: String) {
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
                    "{\"username\":" + "\""+ myApp.name + "\"" + "}"
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
                            mSubmitOrderBtn.isEnabled = false
                            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
                            loadData()
                        } else {
                            Toast.makeText(activity, "Request Wrong!", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun loadData() {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/getCart"
        //Bullet proofing test to make sure connection manager reference is not null
        if (mConnMgr != null) {
            // Get active network info
            val networkInfo = mConnMgr!!.activeNetworkInfo
            //If any activie network is available and inernet connection is available
            if (networkInfo != null) { // && networkInfo.isConnected
                //Start to data download by coroutine
                loadDataByCoroutines(path)
            } else {
                //If network is off of Internet is not availble, inform the user
                Toast.makeText(activity, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDataByCoroutines(path: String) {
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
                    "{\"username\":" + "\""+ myApp.name + "\"" + "}"
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
                        if (successBoolean) {
                            val itemList: JSONArray? = reader.getJSONArray("cartList")
                            if (itemList != null) {
                                if (itemList.length() == 1 ) {
                                    mLoginFirst.text = resources.getString(R.string.nothing_in_cart)
                                    mSubmitOrderBtn.isEnabled = false
                                    list = ArrayList<Map<String, Any>>()
                                    val myListAdapter = activity?.let { CartListAdapter(it, list)}
                                    mCartListView.adapter = myListAdapter
                                } else {
                                    for (i in 1 until itemList.length()) {
                                        val `object`: JSONObject = itemList.getJSONObject(i)
                                        val map = hashMapOf<String, Any>()
                                        try {
                                            val itemId:String = `object`.getString("itemId")
                                            val name:String = `object`.getString("name")
                                            val price:Double = `object`.getDouble("price")
                                            val imgUrl:String = `object`.getString("imgUrl")
                                            val amount: Int = `object`.getInt("amount")

                                            map.put("itemId", itemId)
                                            map.put("name", name)
                                            map.put("price", price)
                                            map.put("imgUrl", imgUrl)
                                            map.put("amount", amount)

                                            list.add(map)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        Log.i("TESTJSON", "----------------")
                                        Log.i("TESTJSON", "id=" + `object`.getString("itemId"))
                                        Log.i("TESTJSON", "name=" + `object`.getString("name"))
                                        Log.i("TESTJSON", "price=" + `object`.getDouble("price"))
                                        Log.i("TESTJSON", "imgUrl=" + `object`.getString("imgUrl"))
                                        Log.i("TESTJSON", "amount=" + `object`.getInt("amount"))
                                    }
                                    val myListAdapter = activity?.let { CartListAdapter(it, list) }
                                    mCartListView.adapter = myListAdapter
                                    mSubmitOrderBtn.isEnabled = true
//                                mCartListView.setOnItemClickListener(){adapterView, view, position, id ->
//                                    val itemAtPos = adapterView.getItemAtPosition(position)
//                                    val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//                                    // Toast.makeText(activity, list.get(position).get("itemId").toString(), Toast.LENGTH_LONG).show()
//                                    val intent = Intent(getActivity()?.getApplicationContext(), DetailActivity::class.java)
//                                    val bundle = Bundle()
//                                    bundle.putString("itemId", list.get(position).get("itemId").toString())
//                                    bundle.putString("name", list.get(position).get("name").toString())
//                                    bundle.putString("price", list.get(position).get("price").toString())
//                                    bundle.putString("imgUrl", list.get(position).get("imgUrl").toString())
//                                    intent.putExtras(bundle)
//                                    startActivity(intent)
//                                }
                                }

                            }
                        } else {
                            Toast.makeText(activity, "Request Wrong!", Toast.LENGTH_LONG).show()
                        }

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