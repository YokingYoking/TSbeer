package com.example.tsbeer

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import java.util.*

class ChangeNicknameActivity : AppCompatActivity() {

    lateinit var etNickName: EditText
    lateinit var etPhone: EditText
    lateinit var etAddress: EditText
    lateinit var mConnMgr: ConnectivityManager
    lateinit var myApp: MyApplication
    lateinit var btnRegister: Button
    lateinit var nickname: String
    lateinit var phone: String
    lateinit var address: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        setTitle(getResources().getText(R.string.change_info))
        mConnMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        myApp = this.application as MyApplication
        etNickName = findViewById(R.id.et_nickName2)
        etPhone = findViewById(R.id.et_phone2)
        etAddress = findViewById(R.id.et_address2)
        btnRegister = findViewById(R.id.btn_register2)
        btnRegister.setOnClickListener(View.OnClickListener {
            nickname = etNickName.text.toString()
            phone = etPhone.text.toString()
            address = etAddress.text.toString()
            loadData(nickname, phone, address)
        })
    }

    private fun loadData(nickname: String, phone: String, address: String) {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/changeInfo"
        //Bullet proofing test to make sure connection manager reference is not null
        if (mConnMgr != null) {
            // Get active network info
            val networkInfo = mConnMgr!!.activeNetworkInfo
            //If any activie network is available and inernet connection is available
            if (networkInfo != null) { // && networkInfo.isConnected
                //Start to data download by coroutine
                loadDataByCoroutines(path, nickname, phone, address)
            } else {
                //If network is off of Internet is not availble, inform the user
                Toast.makeText(this@ChangeNicknameActivity, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDataByCoroutines(path: String, nickname: String, phone: String, address: String) {
        GlobalScope.launch(Dispatchers.IO) {
            var data: String? = null
            val inStream: InputStream
            val outStream: OutputStream
            // create a URL Connection object and set its parameters
            val url = URL(path)
            val urlConn = url.openConnection() as HttpURLConnection
            try {
                urlConn.connectTimeout = 600
                urlConn.readTimeout = 2500
                // Set HTTP request method
                urlConn.requestMethod = "POST"
                urlConn.doInput = true
                urlConn.doOutput = true
                urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8")
                //Perform network request
                urlConn.connect()
                val body =
                    "{\"username\":" + "\"" + myApp.name + "\"" + "," + "\"nickname\":"+ "\"" + nickname + "\"," + "\"phone\":" + "\"" + phone + "\"," +"\"address\":" + "\"" + address + "\"" + "}";
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
                        val loginMessage: String = reader.getString("message")
                        if (successBoolean) {
                            myApp.nickname = nickname
                            Toast.makeText(this@ChangeNicknameActivity, loginMessage, Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@ChangeNicknameActivity, loginMessage, Toast.LENGTH_LONG).show()
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

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> finish()
        }
        return true
    }

}