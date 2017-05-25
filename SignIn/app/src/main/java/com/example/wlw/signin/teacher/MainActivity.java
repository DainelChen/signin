package com.example.wlw.signin.teacher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.example.wlw.signin.MySingleton;
import com.example.wlw.signin.R;
import com.example.wlw.signin.controller.ActivityController;
import com.example.wlw.signin.controller.BaseActivity;
import com.example.wlw.signin.mapActivity;
import com.example.wlw.signin.utils.HttpUtils;
import com.example.wlw.signin.utils.utils;
import com.baidu.mapapi.*;
import com.tencent.bugly.Bugly;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WLW on 2017/5/15.
 */

public class MainActivity extends AppCompatActivity {
    private LocationClient mLocationClient;


    private double longitude;
    private double latitude;


    private double myLongitude;
    private double myLatitude;

    private int radius;
    private int mode;
    private String mac;


    @BindView(R.id.myinfo)
    Button myinfo;
    @BindView(R.id.mylocation_map)
    Button mylocationMap;
    @BindView(R.id.worksign)
    Button worksign;
    private String BASR_URL = utils.BASE + "/signin/InsignServlet?method=signin";
    private String FIND_WAY_SIGN = utils.BASE + "/signin/WaySignServlet?method=findWaySign";
    private String BASE_BSSID = "28:f0:76:18:82:f6";
    private String MY_BSSID;

    private String tid;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        Bugly.init(getApplicationContext(), "9530eb2096", true);


        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        MY_BSSID = utils.getBSSID(getBaseContext());
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("签到中");
        progressDialog.setCanceledOnTouchOutside(false);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

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


                HttpUtils jsonObjectRequest = new HttpUtils(Request.Method.POST,
                        FIND_WAY_SIGN, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            int status = jsonObject.getInt("status");
                            if (status == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                longitude = data.getDouble("longitude");
                                latitude = data.getDouble("latitude");
                                radius = data.getInt("radius");
                                mode = data.getInt("mode");
                                mac = data.getString("mac");

                                tosign();
                                //Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

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

    private void tosign() {
        if (mode == 0) {//定位签到
            SDKInitializer.initialize(getApplicationContext());
            mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {

                    myLatitude = bdLocation.getLatitude();
                    myLongitude = bdLocation.getLongitude();


                    LatLng center = new LatLng(latitude, longitude);
                    LatLng my = new LatLng(myLatitude, myLongitude);

                    if (SpatialRelationUtil.isCircleContainsPoint(center, radius, my)) {
                        requestSign();
                    } else {
                        dialog("您不在指定范围内");


                    }


                }

                @Override
                public void onConnectHotSpotMessage(String s, int i) {

                }
            });


            // 声明权限，将权限添加到list集合中再一次性申请
            List<String> permissionList = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
            } else {
                requestLocation();
            }

        }
        if (mode == 1) {//路由签到

            MY_BSSID = utils.getBSSID(getBaseContext());

            if (MY_BSSID == null) {
                Toast.makeText(this, "请连接指定的WIFI路由进行签到", Toast.LENGTH_SHORT).show();
                return;

            }
            if (!MY_BSSID.equals(BASE_BSSID)) {
                Toast.makeText(this, "请连接指定的WIFI路由进行签到", Toast.LENGTH_SHORT).show();
            } else {

                requestSign();


            }


        }


    }

    private void requestSign() {


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

    public boolean onOptionsItemSelected(MenuItem item) {
        //点击back键finish当前activity
        switch (item.getItemId()) {
            case android.R.id.home:
                //  finish();
                break;
        }
        return true;
    }


    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(500);// 设置发起定位请求的时间间隔 单位ms 0表示定位一次
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setOpenGps(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向

        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            default:
                break;
        }
    }

}
