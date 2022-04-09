package com.example.tsbeer

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class DetailActivity : AppCompatActivity() {

    lateinit var mAddToCartBtn: Button
    lateinit var mConnMgr: ConnectivityManager
    lateinit var itemId: String
    lateinit var mSpinner: Spinner
    lateinit var amountArray: Array<String>
    var amount: Int = 1
    lateinit var myApp: MyApplication
    lateinit var mStockTv: TextView
    lateinit var mDescriptionTv: TextView
    lateinit var mRatingBar: RatingBar
    lateinit var mScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        setTitle(getResources().getText(R.string.detail_title))

        mConnMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        myApp = this.application as MyApplication

        val nameTV : TextView = findViewById<TextView>(R.id.detailName)
        val priceTV : TextView = findViewById<TextView>(R.id.detailPrice)
        val resultImage : ImageView = findViewById<ImageView>(R.id.detailImg)
        mStockTv = findViewById(R.id.stockTv)
        mDescriptionTv = findViewById(R.id.descriptionTv)

        val bundle = intent.extras
        itemId = bundle!!.getString("itemId")!!
        val name = bundle.getString("name")!!
        val price = bundle.getString("price")!!
        val imgUrl = bundle.getString("imgUrl")!!
        val stock = bundle.getString("stock")!!
        val description = bundle.getString("description")
        val score = bundle.getString("score")

        nameTV.text = name
        priceTV.text = "￥ " + price
        mStockTv.text = "Stock: " + stock
        mDescriptionTv.text = description


        GlobalScope.launch(Dispatchers.IO) {
            val myurl = URL(imgUrl)
            val httpURLConnection = myurl.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            var bitmap = BitmapFactory.decodeStream(httpURLConnection.inputStream)
            launch(Dispatchers.Main) {
                resultImage.setImageBitmap(bitmap)
            }
        }

        mAddToCartBtn = findViewById(R.id.addCartBtn)
        mAddToCartBtn.setOnClickListener {
            if (myApp.name == "") {
                Toast.makeText(this, R.string.login_first, Toast.LENGTH_LONG).show()
            } else {
                postData()
            }
        }

        mSpinner = findViewById(R.id.amountSpinner)
        amountArray = resources.getStringArray(R.array.amount)

        val adapterAmount = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, amountArray)
        mSpinner.adapter = adapterAmount
        mSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        arg2: Int, arg3: Long) {
                amount = arg0.selectedItemPosition + 1
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        mRatingBar = findViewById(R.id.ratingBar)
        if (score != null) {
            mRatingBar.rating = score.toFloat()
        }
        mScore = findViewById(R.id.score)
        mScore.text = score
        mRatingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                mScore.text = rating.toString()
                Toast.makeText(this, "Rate Successfully", Toast.LENGTH_LONG).show()
            }
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun postData() {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/addCart"
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
                Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDataByCoroutines(path: String) {
        GlobalScope.launch(Dispatchers.IO) {
            var data: String? = null
            val inStream: InputStream
            val outStream: OutputStream
            try {
                // create a URL Connection object and set its parameters
                val url = URL(path)
                val urlConn = url.openConnection() as HttpURLConnection
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
                    "{\"username\":" + "\"" + myApp.name +"\"" + ",\"amount\":" + amount.toString() + ",\"itemId\":" + "\"" + itemId + "\"}"
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
            }
            launch(Dispatchers.Main) {
                if (data != null) {
                    try {
                        val reader = JSONObject(data)
                        val successBoolean: Boolean = reader.getBoolean("success")
                        if (successBoolean) {
                            val msg: String = reader.getString("message")
                            Log.i("internet", msg)
                            Toast.makeText(applicationContext, "Add to cart Successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(applicationContext, "Request Wrong!", Toast.LENGTH_LONG).show()
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