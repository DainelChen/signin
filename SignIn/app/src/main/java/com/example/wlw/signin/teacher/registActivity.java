package com.example.wlw.signin.teacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by WLW on 2017/5/14.
 */

public class registActivity extends BaseActivity {

    private ProgressDialog progressDialog;

    public static final String BASE_URL = utils.BASE + "/signin/TeacherServlet?method=regist";
    @BindView(R.id.snumber_edit)
    EditText snumberEdit;
    @BindView(R.id.spass_edit)
    EditText spassEdit;
    @BindView(R.id.registclick)
    Button registclick;
    String IMEI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.regist_layout);
        ButterKnife.bind(this);
        IMEI = utils.getIMEI(getBaseContext());

        progressDialog = new ProgressDialog(registActivity.this);
        progressDialog.setMessage("注册中");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @OnClick(R.id.registclick)
    public void onViewClicked() {

        if (!checkInput()) {
            Log.v("", "---");
            Toast.makeText(registActivity.this, "工号或者密码长度不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        progressDialog.show();
        String url = BASE_URL + "&tnumber=" + snumberEdit.getText().toString() + "&tpass=" + spassEdit.getText().toString() + "&tuuid=" + IMEI;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 200) {
                                Intent intent = new Intent(registActivity.this, LoginActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                            } else {
                                String msg = response.getString("msg");

                                Toast.makeText(registActivity.this, msg, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();

                            Toast.makeText(registActivity.this, "错误", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        Toast.makeText(registActivity.this, "错误", Toast.LENGTH_SHORT).show();


                    }
                }
                );
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private boolean checkInput() {
        Log.v("length", "" + snumberEdit.getText().length());
        Log.v("length", "" + spassEdit.getText().length());
        if (snumberEdit.getText().length() == 10) {
            if (spassEdit.getText().length() <= 18 && spassEdit.getText().length() >= 6) {
                return true;
            }
        }
        return false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

}

