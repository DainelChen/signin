//package com.example.wlw.signin.teacher;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.example.wlw.signin.MySingleton;
//import com.example.wlw.signin.R;
//import com.example.wlw.signin.controller.ActivityController;
//import com.example.wlw.signin.utils.HttpUtils;
//import com.example.wlw.signin.utils.utils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * Created by WLW on 2017/5/15.
// */
//
//public class myInfoActivity extends Activity {
//    //(tid,tname,tnumber,tpass,tuuid,tsex,tage,aid)
//    final String items[] = new String[]{"信息工程学院", "人文学院", "师范学院", "艺术学院", "机械材料学院", "生物工程学院"};
//
//    private final String BASE_URL = utils.BASE + "/signin/TeacherServlet?method=update";
//
//    //工号
//    @BindView(R.id.editText_tnum)
//    EditText editTextTnum;
//    @BindView(R.id.show_tnum)
//    TextView showTnum;
//
//    //姓名
//    @BindView(R.id.show_tname)
//    TextView showTname;
//    @BindView(R.id.editText_tname)
//    EditText editTextTname;
//
//    //学院文字
//    @BindView(R.id.show_tc)
//    TextView showTc;
//    @BindView(R.id.show_tsex)
//    TextView showTsex;
//
//    @BindView(R.id.button3)
//    Button button3;
//
//
//    private ProgressDialog progressDialog;
//    private boolean isEdit;
//
//    private String tid;
//    private String tnumber;
//    private String tsex;
//
//    private String tname;
//
//    private String tc_s;
//    private int tc_i;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ActivityController.addActivity(this);
//        setContentView(R.layout.myinfo_layout);
//        ButterKnife.bind(this);
//        progressDialog = new ProgressDialog(myInfoActivity.this);
//        progressDialog.setMessage("修改中");
//        progressDialog.setCanceledOnTouchOutside(false);
//
//
//        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
//        tid = sharedPreferences.getString("tid", "未设置");
//        tsex = sharedPreferences.getString("tsex", "未设置");
//        tc_s = sharedPreferences.getString("aid", "未设置");
//        tnumber = sharedPreferences.getString("tnumber", "未设置");
//        tname = sharedPreferences.getString("tname", "未设置");
//
//
//        //editTextTnum.setText(tnumber);
//        showTnum.setText(tnumber);
//
//
//        showTsex.setText(tsex.equals("null") ? "未设置" : tsex);
//        showTc.setText(tc_s.equals("null") ? "未设置" : items[Integer.parseInt(tc_s) < 10 ? Integer.parseInt(tc_s) : 0 ]);
//        showTname.setText(tname.equals("null") ? "未设置" : tname);
//        editTextTname.setText(tname.equals("null") ? "未设置" : tname);
//
//        editTextTnum.setEnabled(false);
//        editTextTname.setEnabled(false);
//
//        showTc.setEnabled(false);
//        showTsex.setEnabled(false);
//
//
//        isEdit = false;
//    }
//
//    @OnClick(R.id.button3)
//    public void onViewClicked() {
//        if (!isEdit) {
//            //editTextTnum.setVisibility(View.VISIBLE);
//            editTextTname.setVisibility(View.VISIBLE);
//
//
//            showTname.setVisibility(View.INVISIBLE);
//            // showTnum.setVisibility(View.INVISIBLE);
//            // showTc.setVisibility(View.INVISIBLE);
//            //showTsex.setVisibility(View.INVISIBLE);
//
//            editTextTnum.setEnabled(true);
//            editTextTname.setEnabled(true);
//
//            showTc.setEnabled(true);
//            showTsex.setEnabled(true);
//
//            button3.setText("完成");
//            isEdit = !isEdit;
//
//
//        } else {
//            showTc.setEnabled(false);
//            showTsex.setEnabled(false);
//
//            editTextTnum.setVisibility(View.INVISIBLE);
//            editTextTname.setVisibility(View.INVISIBLE);
//
//
//            showTname.setVisibility(View.VISIBLE);
//            showTnum.setVisibility(View.VISIBLE);
//            showTc.setVisibility(View.VISIBLE);
//            showTsex.setVisibility(View.VISIBLE);
//
//            button3.setText("编辑");
//            isEdit = !isEdit;
//            updata();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        }
//
//
//    }
//
//    private void updata() {
//
//        updataShow();
//        progressDialog.show();
//        Map<String, String> map = new HashMap<>();
//        map.put("tid", tid);
//        map.put("tname", editTextTname.getText().toString());
//        map.put("tsex", tsex);
//        map.put("aid", tc_s);
//
//        HttpUtils jsonObjectRequest = new HttpUtils(Request.Method.POST,
//                BASE_URL, map, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                try {
//                    int status = jsonObject.getInt("status");
//                    if (status == 200) {
//                        Toast.makeText(myInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//
//                        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("tname", tname);
//                        editor.putString("tsex", tsex);
//                        editor.putString("aid", tc_s);
//                        editor.commit();
//
//                        progressDialog.dismiss();
//                        updataShow();
//                    } else {
//                        Toast.makeText(myInfoActivity.this, "修改出错", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(myInfoActivity.this, "修改出错", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                progressDialog.dismiss();
//                Toast.makeText(myInfoActivity.this, "修改出错", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//
//    }
//
//    private void updataShow() {
//        tname = editTextTname.getText().toString();
//        showTname.setText(tname);
//
//
//    }
//
//
//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//        ActivityController.removeActivity(this);
//
//    }
//
//    @OnClick({R.id.show_tc, R.id.show_tsex})
//    public void onViewClicked(View view) {
//        updataShow();
//        switch (view.getId()) {
//            case R.id.show_tc: {
//                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("学院").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        tc_s = String.valueOf(which);
//                        Log.v("select", "" + which);
//                        showTc.setText(items[Integer.parseInt((tc_s))]);
//
//                        dialog.dismiss();
//                    }
//                }).create();
//                dialog.show();
//
//
//            }
//            break;
//            case R.id.show_tsex: {
//
//                LayoutInflater factory = LayoutInflater.from(this);//提示框
//                final View views = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
//                final EditText edit=(EditText)views.findViewById(R.id.editText1);//获得输入框对象
//                edit.setHint("当前范围"+"米");//输入框默认值
//                new AlertDialog.Builder(this)
//                        .setTitle("无数据,改变范围试试吧")//提示框标题
//                        .setView(views)
//                        .setPositiveButton("确定",//提示框的两个按钮
//                                new android.content.DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        //事件处理
//                                        Log.v("++",""+edit.getText().toString());
//                                    }
//                                }).setNegativeButton("取消", null).create().show();
////                final String items[] = new String[]{"女", "男"};
////                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("性别").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        tsex = items[which];
////                        Log.v("select", "" + which);
////                        showTsex.setText(tsex);
////
////                        dialog.dismiss();
////                    }
////                }).create();
////                dialog.show();
//
//
//            }
//            break;
//        }
//    }
//}
