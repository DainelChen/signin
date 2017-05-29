package com.example.wlw.signin.teacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.wlw.signin.MySingleton;
import com.example.wlw.signin.R;
import com.example.wlw.signin.controller.ActivityController;
import com.example.wlw.signin.controller.BaseActivity;
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

public class mycenter extends BaseActivity {


    @BindView(R.id.myinfo)
    TextView myinfo;
    @BindView(R.id.bindphone)
    TextView bindphone;
    @BindView(R.id.changepsd)
    TextView changepsd;
    @BindView(R.id.loginout)
    TextView loginout;


    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_center);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("修改中");
        progressDialog.setCanceledOnTouchOutside(false);


        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        tid = sharedPreferences.getString("tid", "");
    }


    private ProgressDialog progressDialog;

    private String tid;
    private final String BASE_URL = utils.BASE + "/signin/TeacherServlet?method=update";

    @OnClick({R.id.myinfo, R.id.bindphone, R.id.changepsd, R.id.loginout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.myinfo: {

                Intent intent = new Intent(this, InfoMation.class);
                startActivity(intent);
            }
            break;
            case R.id.bindphone: {
                Toast.makeText(this, "绑定成功", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.changepsd: {


                LayoutInflater factory = LayoutInflater.from(this);//提示框
                final View views = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
                final EditText edit = (EditText) views.findViewById(R.id.editText1);//获得输入框对象
                edit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(this)
                        .setTitle("输入密码")//提示框标题
                        .setView(views)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {


                                        if(edit.getText().toString().length()<6){
                                            Toast.makeText(mycenter.this, "密码必须大于6位", Toast.LENGTH_SHORT).show();

                                            return;

                                        }

                                        progressDialog.show();
                                        Map<String, String> map = new HashMap<>();
                                        map.put("tid", tid);
                                        map.put("tpass", edit.getText().toString());

                                        HttpUtils jsonObjectRequest = new HttpUtils(Request.Method.POST,
                                                BASE_URL, map, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {
                                                try {
                                                    int status = jsonObject.getInt("status");
                                                    if (status == 200) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(mycenter.this, "修改成功", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        String msg = jsonObject.getString("msg");
                                                        Toast.makeText(mycenter.this, msg, Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(mycenter.this, "错误", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                progressDialog.dismiss();
                                                Toast.makeText(mycenter.this, "修改出错", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        MySingleton.getInstance(mycenter.this).addToRequestQueue(jsonObjectRequest);



                                    }
                                }).setNegativeButton("取消", null).create().show();


            }
            break;
            case R.id.loginout: {


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("退出");
                builder.setMessage("真的要退出账号吗？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("islogin");
                        editor.remove("tid");
                        //editor.remove("tnumber");
                        editor.commit();
                        Intent intent = new Intent(mycenter.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
            break;
        }
    }


}
