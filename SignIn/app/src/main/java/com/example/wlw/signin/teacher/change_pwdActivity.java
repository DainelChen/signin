package com.example.wlw.signin.teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.wlw.signin.MySingleton;
import com.example.wlw.signin.R;
import com.example.wlw.signin.controller.BaseActivity;
import com.example.wlw.signin.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;

/**
 * Created by WLW on 2017/5/18.
 */

public class change_pwdActivity extends BaseActivity {
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.editText)
    EditText editText;
    private ProgressDialog progressDialog;

    private String tid;
    private final String BASE_URL = utils.BASE + "/signin/TeacherServlet?method=update";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(change_pwdActivity.this);
        progressDialog.setMessage("修改中");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        if(!checkInput()){
            Toast.makeText(change_pwdActivity.this, "密码长度不正确", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        tid = sharedPreferences.getString("tid", "未设置");
        progressDialog.show();
        String url = BASE_URL +"&tpass="+editText.getText().toString()+"&tid="+tid;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 200) {
                                progressDialog.dismiss();
                                finish();
                            } else {
                                String msg = response.getString("msg");
                                Toast.makeText(change_pwdActivity.this, msg, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(change_pwdActivity.this, "错误", Toast.LENGTH_SHORT).show();


                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(change_pwdActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }
                );
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);




    }

    private boolean checkInput() {
            if (editText.getText().length() <= 18 && editText.getText().length() >= 6) {
                return true;
            }
        return false;
    }


}
