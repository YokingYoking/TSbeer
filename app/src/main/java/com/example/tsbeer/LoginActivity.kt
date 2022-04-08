package com.example.tsbeer

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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


/**
 * 登录页面
 */
class LoginActivity : AppCompatActivity() {
    lateinit var etAccount: EditText
    lateinit var etPassword: EditText
    lateinit var tvRegister: TextView
    lateinit var btnLogin: Button
    lateinit var mConnMgr: ConnectivityManager
    lateinit var myApp: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        setTitle(getResources().getText(R.string.login_title))
        mConnMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        myApp = application as MyApplication
        etAccount = findViewById(R.id.et_account)
        etPassword = findViewById(R.id.et_password)
        tvRegister = findViewById(R.id.tv_register)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        })

        btnLogin.setOnClickListener(View.OnClickListener {
            val account = etAccount.getText().toString()
            val password = etPassword.getText().toString()
            if ("" == account) { //用户名不能为空
                Toast.makeText(this@LoginActivity, R.string.login_account_hint, Toast.LENGTH_LONG)
                    .show()
                return@OnClickListener
            }
            if ("" == password) { //密码为空
                Toast.makeText(this@LoginActivity, R.string.login_password_hint, Toast.LENGTH_LONG)
                    .show()
                return@OnClickListener
            }
            loadData(account, password)
        })
    }

    override fun onStart() {
        super.onStart()
        loadPreferences()
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun loadData(username: String, password: String) {
        //Website URL to which a network request will be sent
        val path = "https://qcb22o.api.cloudendpoint.cn/login"
        //Bullet proofing test to make sure connection manager reference is not null
        if (mConnMgr != null) {
            // Get active network info
            val networkInfo = mConnMgr!!.activeNetworkInfo
            //If any activie network is available and inernet connection is available
            if (networkInfo != null) { // && networkInfo.isConnected
                //Start to data download by coroutine
                loadDataByCoroutines(path, username, password)
            } else {
                //If network is off of Internet is not availble, inform the user
                Toast.makeText(this@LoginActivity, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDataByCoroutines(path: String,username: String, password: String) {
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
                    "{\"username\":" + "\"" + username + "\"" + ",\"password\":" + "\"" + password + "\"}"
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
                            Toast.makeText(this@LoginActivity, loginMessage, Toast.LENGTH_LONG).show()
                            if (myApp.name == "") {
                                myApp.name = username
                            }
                            savePreferences(username, password)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, loginMessage, Toast.LENGTH_LONG).show()
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

    fun savePreferences(username: String, password: String) {
        val pref = getSharedPreferences("TSbeer", Context.MODE_PRIVATE)
        pref.edit().putString("username", username).commit()
        pref.edit().putString("password", password).commit()
    }
    fun loadPreferences() {
        val pref = getSharedPreferences("TSbeer", Context.MODE_PRIVATE)
        etAccount.setText(pref.getString("username", ""))
        etPassword.setText(pref.getString("password", ""))
    }

}
