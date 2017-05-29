package com.example.wlw.signin.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by WLW on 2017/5/17.
 */

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for ( Activity activity : ActivityController.activities){
            Log.v("IN_STACK_MARK",""+activity.getLocalClassName());


        }
        ActivityController.addActivity(this);

        Log.v("BaseActivity",getClass().getSimpleName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for ( Activity activity : ActivityController.activities){
            Log.v("OUT_STACK_MARK",""+activity.getLocalClassName());


        }
        ActivityController.removeActivity(this);

    }
}
