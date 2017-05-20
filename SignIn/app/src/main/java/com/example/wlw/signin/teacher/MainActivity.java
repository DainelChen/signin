package com.example.wlw.signin.teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wlw.signin.MySingleton;
import com.example.wlw.signin.R;
import com.example.wlw.signin.controller.ActivityController;
import com.example.wlw.signin.controller.BaseActivity;
import com.example.wlw.signin.mapActivity;
import com.example.wlw.signin.utils.HttpUtils;
import com.example.wlw.signin.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WLW on 2017/5/15.
 */

public class MainActivity extends BaseActivity {


    @BindView(R.id.myinfo)
    Button myinfo;
    @BindView(R.id.mylocation_map)
    Button mylocationMap;
    @BindView(R.id.worksign)
    Button worksign;
    private String BASR_URL = utils.BASE + "/signin/InsignServlet?method=signin";
    private String BASE_BSSID = "28:f0:76:18:82:f6";
    private String MY_BSSID;

    private String tid;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        MY_BSSID = utils.getBSSID(getBaseContext());
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("签到中");
        progressDialog.setCanceledOnTouchOutside(false);

    }


    @OnClick({R.id.myinfo, R.id.mylocation_map, R.id.worksign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.myinfo: {
                Intent intent = new Intent(this, mycenter.class);
                startActivity(intent);
//                Intent intent = new Intent(this, Preference.class);
//                startActivity(intent);

            }
            break;
            case R.id.mylocation_map: {
                Intent intent = new Intent(this, mapActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.worksign: {
                MY_BSSID = utils.getBSSID(getBaseContext());

                if (MY_BSSID == null) {
                    Toast.makeText(this, "请连接指定的WIFI路由进行签到", Toast.LENGTH_SHORT).show();
                    break;

                }
                if (!MY_BSSID.equals(BASE_BSSID)) {
                    Toast.makeText(this, "请连接指定的WIFI路由进行签到", Toast.LENGTH_SHORT).show();
                    break;
                }


                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                tid = sharedPreferences.getString("tid", "");
                Map<String, String> map = new HashMap<>();
                map.put("tid", tid);

                HttpUtils jsonObjectRequest = new HttpUtils(Request.Method.POST,
                        BASR_URL, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            int status = jsonObject.getInt("status");
                            if (status == 200) {
                                //Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                dialog("签到成功");
                            } else {
                                // Toast.makeText(MainActivity.this, "签到失败", Toast.LENGTH_SHORT).show();
                                String msg = jsonObject.getString("msg");

                                progressDialog.dismiss();
                                dialog(msg);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        dialog("签到失败");
                    }
                });
             MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

            }
            break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    public void onBackPressed() {
        ActivityController.finshAll();
    }


    public void dialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
