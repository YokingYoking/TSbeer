package com.example.tsbeer;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tsbeer.R;
import com.example.tsbeer.bean.User;
import com.example.tsbeer.utils.BirthUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

/**
 * 注册页面
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etAccount;//账号
    private EditText etNickName;//昵称
    private EditText etPhone;//手机号
    private EditText etIdentityCard;//身份证号
    private EditText etAddress;//地址
    private EditText etPassword;//密码
    private EditText etPasswordSure;//确认密码
    private ImageView ivPhoto;//头像
    private RadioGroup rgSex;//性别
    private TextView tvLogin;//登录
    private Button btnRegister;//注册按钮
    private String imagePath = "";
    private ConnectivityManager mConnMgr;
    // private MyApplication myApp;
    private RequestOptions headerRO = new RequestOptions().circleCrop();//圆角变换

    private String account;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getText(R.string.register_title));
        mConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // myApp = (MyApplication) getApplication();
        etAccount = findViewById(R.id.et_account);//获取账号
        etNickName = findViewById(R.id.et_nickName);//获取昵称
        etIdentityCard = findViewById(R.id.et_identityCard);//获取身份证号
        etPhone = findViewById(R.id.et_phone);//获取手机号
        etAddress = findViewById(R.id.et_address);//获取地址
        etPassword = findViewById(R.id.et_password);//获取密码
        etPasswordSure = findViewById(R.id.et_password_sure);//获取确认密码
        ivPhoto = findViewById(R.id.iv_photo);
        rgSex = findViewById(R.id.rg_sex);
        tvLogin = (TextView) findViewById(R.id.tv_login);//登录
        btnRegister = (Button) findViewById(R.id.btn_register);//获取注册按钮
        //从相册中选择头像
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectClick();
            }
        });
        //返回登录
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到登录页面
//                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
                finish();
            }
        });
        //设置注册点击按钮
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regex = "\\d{15}|\\d{17}[\\dxX]";
                //获取请求参数
                account = etAccount.getText().toString();
                String nickName = etNickName.getText().toString();
                String identityCard = etIdentityCard.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                password = etPassword.getText().toString();
                String passwordSure = etPasswordSure.getText().toString();
                if ("".equals(account)) {//用户名不能为空
                    Toast.makeText(RegisterActivity.this,   R.string.register_account_hint, Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(nickName)) {//昵称不能为空
                    Toast.makeText(RegisterActivity.this, R.string.register_name_hint, Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(identityCard)) {//身份证号不能为空
                    Toast.makeText(RegisterActivity.this, R.string.register_id_number_hint, Toast.LENGTH_LONG).show();
                    return;
                }
                if(!identityCard.matches(regex)) {//身份证号错误
                    Toast.makeText(RegisterActivity.this, R.string.register_id_number_error, Toast.LENGTH_LONG).show();
                    return;
                }
                int age = BirthUtils.getAge(identityCard);//获取年龄
                if (age <18 ){//未成年
                    Toast.makeText(RegisterActivity.this, R.string.register_nonage, Toast.LENGTH_LONG).show();
                    return;
                }

                if ("".equals(phone)) {//手机号不能为空
                    Toast.makeText(RegisterActivity.this, R.string.register_phone_hint, Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(address)) {//地址不能为空
                    Toast.makeText(RegisterActivity.this, R.string.register_address_hint, Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(password)) {//密码为空
                    Toast.makeText(RegisterActivity.this, R.string.register_password_hint, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(passwordSure)) {//密码不一致
                    Toast.makeText(RegisterActivity.this, R.string.register_same, Toast.LENGTH_LONG).show();
                    return;
                }
                String sex = rgSex.getCheckedRadioButtonId() == R.id.rb_man ?
                        RegisterActivity.this.getString(R.string.register_gender_man) :
                        RegisterActivity.this.getString(R.string.register_gender_woman);//性别
                loadData();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();// finish your activity
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 选择图片
     */
    private void selectClick() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        for (int i = 0; i < result.size(); i++) {
                            // onResult Callback
                            LocalMedia media = result.get(i);
                            String path;
                            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                            boolean compressPath = media.isCompressed() || (media.isCut() && media.isCompressed());
                            // 裁剪过
                            boolean isCutPath = media.isCut() && !media.isCompressed();

                            if (isCutPath) {
                                path = media.getCutPath();
                            } else if (compressPath) {
                                path = media.getCompressPath();
                            } else if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                                // AndroidQ特有path
                                path = media.getAndroidQToPath();
                            } else if (!TextUtils.isEmpty(media.getRealPath())) {
                                // 原图
                                path = media.getRealPath();
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    path = PictureFileUtils.getPath(RegisterActivity.this, Uri.parse(media.getPath()));
                                } else {
                                    path = media.getPath();
                                }
                            }
                            imagePath = path;
                        }
                        Glide.with(RegisterActivity.this).load(imagePath).into(ivPhoto);

                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }

    public void loadData() {
       String path = "https://qcb22o.api.cloudendpoint.cn/createUser";
       if(mConnMgr != null) {
           NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
           if(networkInfo != null) { //  && networkInfo.isConnected()
               Log.i("tag", "register");
               new DownloadDataTask().execute(path);
           } else {
               Toast.makeText(this,"Network Not Available", Toast.LENGTH_LONG);
           }
       }
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...urls) {
            return downloadData(urls[0], account, password);
        }

        protected void onPostExecute(String data) {
            if(data != null) {
                try {
                    JSONObject reader = new JSONObject(data);
                    Boolean successBoolean = reader.getBoolean("success");
                    String msg = reader.getString("message");
                    if(successBoolean) {
                        toastShowMsg(msg);
                        finish();
                    } else {
                        toastShowMsg(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void toastShowMsg(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_LONG).show();
    }

    public String downloadData(String path, String username, String password) {
        String data = null;
        InputStream inStream;
        OutputStream outStream;
        try {
            URL url = new URL(path);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(2500);
            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            urlConn.connect();
            String body =
                    "{\"username\":" + "\"" + username + "\"" + ",\"password\":" + "\"" + password + "\"}";
            outStream = urlConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
            writer.write(body);
            writer.close();
            inStream = urlConn.getInputStream();
            data = readStream(inStream);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine())!= null) {
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data.toString();
    }


}
