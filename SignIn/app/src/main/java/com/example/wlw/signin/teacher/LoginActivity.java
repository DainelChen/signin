package com.example.wlw.signin.teacher;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.wlw.signin.MySingleton;
import com.example.wlw.signin.R;
import com.example.wlw.signin.controller.ActivityController;
import com.example.wlw.signin.controller.BaseActivity;
import com.example.wlw.signin.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Button login;
    private Button regist;


    private EditText stuNum;
    private EditText stuPas;
    private ProgressDialog progressDialog;
    //http://172.16.120.235:8080/signin/TeacherServlet?method=login&tnumber=?&tpass=?&tuuid=?
    public static final String BASE_URL = utils.BASE + "/signin/TeacherServlet?method=login";
    String IMEI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("登录中");
        progressDialog.setCanceledOnTouchOutside(false);
        login = (Button) findViewById(R.id.login);
        stuNum = (EditText) findViewById(R.id.stunum);
        //stuNum.setText("2511150445");
        stuPas = (EditText) findViewById(R.id.password);
       // stuPas.setText("2812134");
        login.setOnClickListener(this);
        IMEI = utils.getIMEI(getBaseContext());


        regist = (Button) findViewById(R.id.regist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, registActivity.class);
                startActivity(intent);
            }
        });
    }
//    Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what==1){
//                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
//
//
//            }
//        }
//    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login: {
                if (!checkInput()) {
                    Toast.makeText(LoginActivity.this, "工号或者密码长度不正确", Toast.LENGTH_SHORT).show();


                    return;

                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                progressDialog.show();
                String url = BASE_URL + "&tnumber=" + stuNum.getText().toString() + "&tpass=" + stuPas.getText().toString() + "&tuuid=" + IMEI;

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 200) {
                                        JSONObject data = response.getJSONObject("data");

                                        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("islogin", true);
                                        editor.putString("tid", data.getString("tid"));
                                        editor.putString("tname", data.getString("tname"));
                                        editor.putString("tnumber", data.getString("tnumber"));
                                        editor.putString("tsex", data.getString("tsex"));
                                        editor.putString("aid", data.getString("aid"));
                                        editor.commit();
                                        progressDialog.dismiss();


                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    } else {
                                        String msg = response.getString("msg");
                                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "错误", Toast.LENGTH_SHORT).show();


                                }
                            }
                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                        );
                MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

            }


        }
    }

    private void parsedata(JSONObject data) throws JSONException {
        String sid = data.getString("sid");
    }


    private boolean isExit;

    /**
     * 程序是否退出
     */
    private boolean isFinish;
    private final int CODE_NOT_EXIT = 0x000;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CODE_NOT_EXIT:
                    // 程序退出后不再做操作
                    if (isFinish) break;
                    isExit = false;
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);

        isFinish = true;
    }

    @Override
    public void onBackPressed() {
        ActivityController.finshAll();
    }

    private boolean checkInput() {

        if (stuNum.getText().length() == 10) {
            if (stuPas.getText().length() <= 18 && stuNum.getText().length() >= 6) {
                return true;
            }
        }
        return false;
    }

}