package com.example.tsbeer.ui.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tsbeer.DetailActivity
import com.example.tsbeer.R
import com.example.tsbeer.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern

class HomeFragment : Fragment() {
    // connectivity Manager instance
    lateinit var mConnMgr: ConnectivityManager
    lateinit var mitemListView: ListView
    lateinit var mSearchView: SearchView
    private var list = ArrayList<Map<String, Any>>()

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Store the Connectivity Manager in the member variable
        mConnMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        loadData()
        mitemListView = binding.itemListView

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSearchView = binding.searchView
        mSearchView.setIconified(false)
        mSearchView.setSubmitButtonEnabled(true)
        mSearchView.setQueryHint("Search item name...")
        mSearchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                Log.i("CSDN_LQR", "TextSubmit : " + s)
                val p:Pattern = Pattern.compile(s)
                if(s == "") {
                    val myListAdapter = activity?.let { MyListAdapter(it, list) }!!
                    mitemListView.adapter = myListAdapter
                    mitemListView.setOnItemClickListener(){adapterView, view, position, id ->
                        val itemAtPos = adapterView.getItemAtPosition(position)
                        val itemIdAtPos = adapterView.getItemIdAtPosition(position)
                        // Toast.makeText(activity, list.get(position).get("itemId").toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(getActivity()?.getApplicationContext(), DetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("itemId", list.get(position).get("itemId").toString())
                        bundle.putString("name", list.get(position).get("name").toString())
                        bundle.putString("price", list.get(position).get("price").toString())
                        bundle.putString("imgUrl", list.get(position).get("imgUrl").toString())
                        bundle.putString("stock", list.get(position).get("stock").toString())
                        bundle.putString("description", list.get(position).get("description").toString())
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                    return false
                }
                val filterItemList = ArrayList<Map<String, Any>>()
                for (i in 0 until list.size) {
                    val item = list.get(i)
                    val matcher: Matcher = p.matcher(item.get("name").toString())
                    if (matcher.find()) {
                        filterItemList.add(item)
                    }
                    val myListAdapter = activity?.let { MyListAdapter(it, filterItemList) }!!
                    mitemListView.adapter = myListAdapter
                    mitemListView.setOnItemClickListener(){adapterView, view, position, id ->
                        val intent = Intent(getActivity()?.getApplicationContext(), DetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("itemId", list.get(position).get("itemId").toString())
                        bundle.putString("name", list.get(position).get("name").toString())
                        bundle.putString("price", list.get(position).get("price").toString())
                        bundle.putString("imgUrl", list.get(position).get("imgUrl").toString())
                        bundle.putString("stock", list.get(position).get("stock").toString())
                        bundle.putString("description", list.get(position).get("description").toString())
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
                return false
            }
            override fun onQueryTextChange(s: String?): Boolean {
                Log.i("CSDN_LQR", "TextSubmit : " + s)
                val p:Pattern = Pattern.compile(s)
                if(s == "") {
                    val myListAdapter = activity?.let { MyListAdapter(it, list) }!!
                    mitemListView.adapter = myListAdapter
                    mitemListView.setOnItemClickListener(){adapterView, view, position, id ->
                        val intent = Intent(getActivity()?.getApplicationContext(), DetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("itemId", list.get(position).get("itemId").toString())
                        bundle.putString("name", list.get(position).get("name").toString())
                        bundle.putString("price", list.get(position).get("price").toString())
                        bundle.putString("imgUrl", list.get(position).get("imgUrl").toString())
                        bundle.putString("stock", list.get(position).get("stock").toString())
                        bundle.putString("description", list.get(position).get("description").toString())
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                    return false
                }
                val filterItemList = ArrayList<Map<String, Any>>()
                for (i in 0 until list.size) {
                    val item = list.get(i)
                    val matcher: Matcher = p.matcher(item.get("name").toString())
                    if (matcher.find()) {
                        filterItemList.add(item)
                    }
                    val myListAdapter = activity?.let { MyListAdapter(it, filterItemList) }!!
                    mitemListView.adapter = myListAdapter
                    mitemListView.setOnItemClickListener{adapterView, view, position, id ->
                        val intent = Intent(getActivity()?.getApplicationContext(), DetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("itemId", filterItemList.get(position).get("itemId").toString())
                        bundle.putString("name", filterItemList.get(position).get("name").toString())
                        bundle.putString("price", filterItemList.get(position).get("price").toString())
                        bundle.putString("imgUrl", filterItemList.get(position).get("imgUrl").toString())
                        bundle.putString("stock", filterItemList.get(position).get("stock").toString())
                        bundle.putString("description", filterItemList.get(position).get("description").toString())
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData() {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/getItemList"
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
            // create a URL Connection object and set its parameters
            val url = URL(path)
            val urlConn = url.openConnection() as HttpURLConnection
            try {
                urlConn.connectTimeout = 5000
                urlConn.readTimeout = 2500
                // Set HTTP request method
                urlConn.requestMethod = "GET"
                urlConn.doInput = true
                //Perform network request
                urlConn.connect()
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
                            val itemList: JSONArray? = reader.getJSONArray("itemList")
                            if (itemList != null) {
                                for (i in 0 until itemList.length()) {
                                    val `object`: JSONObject = itemList.getJSONObject(i)
                                    val map = hashMapOf<String, Any>()
                                    try {
                                        val itemId:String = `object`.getString("itemId")
                                        val name:String = `object`.getString("name")
                                        val price:Double = `object`.getDouble("price")
                                        val imgUrl:String = `object`.getString("imgUrl")
                                        val description:String = `object`.getString("description")
                                        val stock: Int = `object`.getInt("stock")

                                        map.put("itemId", itemId)
                                        map.put("name", name)
                                        map.put("price", price)
                                        map.put("imgUrl", imgUrl)
                                        map.put("description", description)
                                        map.put("stock", stock)

                                        list.add(map)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    Log.i("TESTJSON", "----------------")
                                    Log.i("TESTJSON", "id=" + `object`.getString("itemId"))
                                    Log.i("TESTJSON", "name=" + `object`.getString("name"))
                                    Log.i("TESTJSON", "price=" + `object`.getDouble("price"))
                                    Log.i("TESTJSON", "imgUrl=" + `object`.getString("imgUrl"))
                                }
                                val myListAdapter = activity?.let { MyListAdapter(it, list) }!!
                                mitemListView.adapter = myListAdapter
                                mitemListView.setOnItemClickListener(){adapterView, view, position, id ->
                                    // Toast.makeText(activity, list.get(position).get("itemId").toString(), Toast.LENGTH_LONG).show()
                                    val intent = Intent(getActivity()?.getApplicationContext(), DetailActivity::class.java)
                                    val bundle = Bundle()
                                    bundle.putString("itemId", list.get(position).get("itemId").toString())
                                    bundle.putString("name", list.get(position).get("name").toString())
                                    bundle.putString("price", list.get(position).get("price").toString())
                                    bundle.putString("imgUrl", list.get(position).get("imgUrl").toString())
                                    bundle.putString("stock", list.get(position).get("stock").toString())
                                    bundle.putString("description", list.get(position).get("description").toString())
                                    intent.putExtras(bundle)
                                    startActivity(intent)
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