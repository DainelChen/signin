package com.example.wlw.signin.teacher;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.wlw.signin.MySingleton;
import com.example.wlw.signin.R;
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
 * Created by WLW on 2017/5/27.
 */

public class InfoMation extends BaseActivity {
    @BindView(R.id.show_tnum)
    TextView showTnum;
    @BindView(R.id.show_tname)
    TextView showTname;
    @BindView(R.id.show_tc)
    TextView showTc;
    @BindView(R.id.show_tsex)
    TextView showTsex;
    @BindView(R.id.button5)
    Button commit;


    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_layout);
        ButterKnife.bind(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("修改中");
        progressDialog.setCanceledOnTouchOutside(false);


        getInfoformLocal();

    }


    private String tid;//教师id
    private String tnumber;//教师工号
    private String tsex;//教师性别
    private String tname;//教师名字
    private String tacademy;

    private void getInfoformLocal() {
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        tid = sharedPreferences.getString("tid", "");

        tnumber = sharedPreferences.getString("tnumber", "未设置");
        tname = sharedPreferences.getString("tname", "");

        tacademy = sharedPreferences.getString("aid", "");
        tacademy = tacademy.equals("null") ? "0" :tacademy;
        tsex = sharedPreferences.getString("tsex", "未设置");


        showTnum.setText(tnumber);
        showTname.setText(tname.equals("null") ? "未设置" : tname);
        showTc.setText(tacademy.equals("null") ? "未设置" : items[Integer.parseInt(tacademy) < 10 ? Integer.parseInt(tacademy) : 0]);
        showTsex.setText(tsex.equals("null") ? "未设置" : tsex);

    }

    final String items[] = new String[]{"信息工程学院", "人文学院", "师范学院", "艺术学院", "机械材料学院", "生物工程学院"};


    @OnClick({R.id.show_tname, R.id.show_tc, R.id.show_tsex, R.id.button5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_tname: {
                LayoutInflater factory = LayoutInflater.from(this);//提示框
                final View views = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
                final EditText edit = (EditText) views.findViewById(R.id.editText1);//获得输入框对象
                edit.setText(tname.equals("null") ? "" : tname);
                new AlertDialog.Builder(this)
                        .setTitle("输入姓名")//提示框标题
                        .setView(views)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        showTname.setText(edit.getText().toString());
                                        tname = edit.getText().toString();
                                    }
                                }).setNegativeButton("取消", null).create().show();

            }
            break;
            case R.id.show_tc: {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("学院").setSingleChoiceItems(items, Integer.parseInt((tacademy)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tacademy = String.valueOf(which);
                        Log.v("select", "" + which);
                        showTc.setText(items[Integer.parseInt((tacademy))]);
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();


            }
            break;
            case R.id.show_tsex: {
                final String items[] = new String[]{"女", "男"};
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("性别").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tsex = items[which];
                        Log.v("select", "" + which);
                        showTsex.setText(tsex);
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();
            }
            break;
            case R.id.button5: {
                commit();
            }
            break;
        }
    }


    private final String BASE_URL = utils.BASE + "/signin/TeacherServlet?method=update";

    private void commit() {


        progressDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("tid", tid);
        map.put("tname", tname);
        map.put("tsex", tsex);
        map.put("aid", tacademy);

        HttpUtils jsonObjectRequest = new HttpUtils(Request.Method.POST,
                BASE_URL, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        Toast.makeText(InfoMation.this, "修改成功", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tname", tname);
                        editor.putString("tsex", tsex);
                        editor.putString("aid", tacademy);
                        editor.commit();

                        progressDialog.dismiss();
                        finish();


                    } else {
                        Toast.makeText(InfoMation.this, "修改出错", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(InfoMation.this, "修改出错", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(InfoMation.this, "修改出错", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }
}
