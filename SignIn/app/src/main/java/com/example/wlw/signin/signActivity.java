package com.example.wlw.signin;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by WLW on 2017/5/11.
 */

public class signActivity extends Activity {
    //西操场
    private BDLocation A;
    private BDLocation B;
    private BDLocation C;
    private BDLocation D;

    //篮球场
    private BDLocation E;
    private BDLocation F;
    private BDLocation G;
    private BDLocation H;


    private double latitude;
    private double longitude;


    public LocationClient mLocationClient;
    public BDLocation myLocation;


    private Button morningexercise;
    private Button whereAmI;
    private Button backSchool;

    private ProgressDialog progressDialog;

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//
//            }
//        }
//    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_layout);
        progressDialog = new ProgressDialog(signActivity.this);
        progressDialog.setMessage("登录中");
        progressDialog.setCanceledOnTouchOutside(false);
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initLocation();
        mLocationClient = new LocationClient(getApplicationContext());
        getLocation();

        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                loglocation(bdLocation);
                if (bdLocation != null) {
                    checkIsOk(bdLocation);
                    mLocationClient.stop();
                    SharedPreferences sharedPreferences = getSharedPreferences("location", MODE_PRIVATE);
                    //得到SharedPreferences.Editor对象，并保存数据到该对象中
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("Latitude", (float) bdLocation.getLatitude());
                    editor.putFloat("Longitude", (float) bdLocation.getLongitude());
                    //保存key-value对到文件中
                    editor.commit();
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {
                Toast.makeText(signActivity.this, "定位错误", Toast.LENGTH_SHORT).show();

            }
        });

        morningexercise = (Button) findViewById(R.id.morningexercise);
        morningexercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 声明权限，将权限添加到list集合中再一次性申请
                List<String> permissionList = new ArrayList<>();
                if (ActivityCompat.checkSelfPermission(signActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ActivityCompat.checkSelfPermission(signActivity.this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (ActivityCompat.checkSelfPermission(signActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(signActivity.this, permissions, 1);
                } else {

                    if (checkInRightTime()) {
                        showAlert("不在早操时间内,不能签到！");
                        return;
                    }
                    mLocationClient.start();
                    Toast.makeText(signActivity.this, "定位中", Toast.LENGTH_SHORT).show();
                }
            }
        });

        whereAmI = (Button) findViewById(R.id.whereAmI);
        whereAmI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signActivity.this, mapActivity.class);
                startActivity(intent);
            }
        });


    }

    private void loglocation(BDLocation location) {
        //获取定位结果
        StringBuffer sb = new StringBuffer(256);

        sb.append("time : ");
        sb.append(location.getTime());    //获取定位时间

        sb.append("\nerror code : ");
        sb.append(location.getLocType());    //获取类型类型

        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());    //获取纬度信息

        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());    //获取经度信息

        sb.append("\nradius : ");
        sb.append(location.getRadius());    //获取定位精准度

        if (location.getLocType() == BDLocation.TypeGpsLocation) {

            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());    // 单位：公里每小时

            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());    //获取卫星数

            sb.append("\nheight : ");
            sb.append(location.getAltitude());    //获取海拔高度信息，单位米

            sb.append("\ndirection : ");
            sb.append(location.getDirection());    //获取方向信息，单位度

            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

            // 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\noperationers : ");
            sb.append(location.getOperators());    //获取运营商信息

            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (location.getLocType() == BDLocation.TypeServerError) {

            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

        }

        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());    //位置语义化信息

        List<Poi> list = location.getPoiList();    // POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }

        Log.i("BaiduLocationApiDem", sb.toString());
    }


    private void checkIsOk(BDLocation bdLocation) {
        latitude = bdLocation.getLatitude();
        longitude = bdLocation.getLongitude();
        if (latitude < A.getLatitude() && longitude < A.getLongitude() &&
                latitude < B.getLatitude() && longitude > B.getLongitude() &&
                latitude > C.getLatitude() && longitude > C.getLongitude() &&
                latitude > D.getLatitude() && longitude < D.getLongitude()//在操场
                ) {

            progressDialog.show();
            String url = "";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int status = response.getInt("status");
                                if (status == 200) {
                                    progressDialog.dismiss();

                                } else {
                                    progressDialog.dismiss();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
                    );
            MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        } else {//不在操场或不在时间段内
            showAlert("不在操场范围内,不能签到！");
        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(signActivity.this);
//        builder.setMessage("确认退出吗？");
//        builder.setTitle("提示");
//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
    }

    private boolean checkInRightTime() {
        Calendar c = Calendar.getInstance(); // 默认得到的是当前的日期
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 7 && hour < 8) {//7-8点之间
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(signActivity.this);
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

    private void initLocation() {
        A = new BDLocation();
        B = new BDLocation();
        C = new BDLocation();
        D = new BDLocation();
        E = new BDLocation();
        F = new BDLocation();
        G = new BDLocation();
        H = new BDLocation();


        A.setLatitude(34.223392);
        A.setLongitude(108.913461);

        B.setLatitude(34.223377);
        B.setLongitude(108.912512);

        C.setLatitude(34.221963);
        C.setLongitude(108.912561);

        D.setLatitude(34.221862);
        D.setLongitude(108.913555);

        E.setLatitude(34.223814);
        E.setLongitude(108.911578);

        F.setLatitude(34.222915);
        F.setLongitude(108.911057);

        G.setLatitude(34.223374);
        G.setLongitude(108.912389);

        H.setLatitude(34.222296);
        H.setLongitude(108.912412);
    }

    public void getLocation() {
        // 声明定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(0);// 设置发起定位请求的时间间隔 单位ms 0表示定位一次
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setOpenGps(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        // 设置定位参数
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
                            Toast.makeText(signActivity.this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    mLocationClient.start();
                    Toast.makeText(signActivity.this, "定位中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(signActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }


}




