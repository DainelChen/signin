package com.example.wlw.signin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.wlw.signin.controller.ActivityController;
import com.example.wlw.signin.controller.BaseActivity;
import com.example.wlw.signin.teacher.LoginActivity;
import com.example.wlw.signin.teacher.MainActivity;
import com.tencent.bugly.Bugly;

/**
 * Created by WLW on 2017/5/15.
 */

public class startActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityController.addActivity(this);
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        boolean islogin = sharedPreferences.getBoolean("islogin", false);
        if (!islogin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
