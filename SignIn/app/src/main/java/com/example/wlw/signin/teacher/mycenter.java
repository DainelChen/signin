package com.example.wlw.signin.teacher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wlw.signin.R;
import com.example.wlw.signin.controller.ActivityController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WLW on 2017/5/15.
 */

public class mycenter extends Activity {


    @BindView(R.id.myinfo)
    TextView myinfo;
    @BindView(R.id.bindphone)
    TextView bindphone;
    @BindView(R.id.changepsd)
    TextView changepsd;
    @BindView(R.id.loginout)
    TextView loginout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);

        setContentView(R.layout.my_center);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.myinfo, R.id.bindphone, R.id.changepsd, R.id.loginout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.myinfo: {
                Intent intent = new Intent(this, myInfoActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.bindphone: {

                Toast.makeText(this, "绑定成功", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.changepsd: {
                Intent intent = new Intent(this, change_pwdActivity.class);
                startActivity(intent);

            }
            break;
            case R.id.loginout: {

                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("islogin");
                editor.remove("tid");
                editor.remove("tnumber");
                editor.commit();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
