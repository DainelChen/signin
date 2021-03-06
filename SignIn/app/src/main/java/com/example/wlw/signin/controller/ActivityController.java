package com.example.wlw.signin.controller;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

/**
 * Created by WLW on 2017/5/17.
 */

public class ActivityController {

    public static List<Activity> activities = new ArrayList<Activity>();

    public  static void  addActivity  (Activity activity){
        activities.add(activity);
    }

    public  static void  removeActivity  (Activity activity){
        activities.remove(activity);
    }

    public  static void  finshAll  (){
        for (Activity activity : activities){
            if (!activity.isFinishing()) activity.finish();

        }
    }
}
