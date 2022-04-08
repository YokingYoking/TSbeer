package com.example.tsbeer

import android.R.attr.delay
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class SubmitActivity : AppCompatActivity() {

    lateinit var mDatePickerBtn: Button
    lateinit var mDeliverDate: TextView
    lateinit var mSubmitBtn: Button
    lateinit var myApp: MyApplication
    lateinit var mConnMgr: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        setTitle(getResources().getText(R.string.submit_order))

        myApp = this.application as MyApplication
        mDatePickerBtn = findViewById(R.id.pickDateBtn)
        mDeliverDate = findViewById(R.id.deliverDateTv)
        mSubmitBtn = findViewById(R.id.submitOrderBtn2)
        mConnMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val listener =
            OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                mDeliverDate.text = mDeliverDate.text.toString() + year + "/" + monthOfYear + "/" + dayOfMonth
            }


        mDatePickerBtn.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance();
            val dialog: DatePickerDialog = DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }

        mSubmitBtn.setOnClickListener {
            this.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle(R.string.submit_order)
                    .setMessage(R.string.submit_dialog)
                    .setPositiveButton("OK") {dialog, which ->
                        mSubmitBtn.isEnabled = false
                        delData()
                        Handler().postDelayed(Runnable {
                            finish()
                        }, 3000)

                    }
                    .setNegativeButton("CANCEL") {dialog, which ->

                    }
                    .show()
            }
        }
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
                Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show()
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
                            mSubmitBtn.isEnabled = false
                            Toast.makeText(this@SubmitActivity, msg, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@SubmitActivity, "Request Wrong!", Toast.LENGTH_LONG).show()
                        }
                        mSubmitBtn.isEnabled = true

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